package edu.cqupt.apibackend.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.apibackend.mapper.UserMapper;
import edu.cqupt.apibackend.service.UserService;
import edu.cqupt.apicommon.common.enums.ResponseCode;
import edu.cqupt.apicommon.common.enums.UserAccountStatusEnum;
import edu.cqupt.apicommon.common.exception.BusinessException;
import edu.cqupt.apicommon.common.util.RedissonLockUtil;
import edu.cqupt.apicommon.model.dto.user.*;
import edu.cqupt.apicommon.model.entity.User;
import edu.cqupt.apicommon.model.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

import static edu.cqupt.apibackend.common.constant.EmailConstant.CAPTCHA_CACHE_KEY;
import static edu.cqupt.apibackend.common.constant.UserConstant.*;


/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	private static final String EMAIL_PATTERN = "^\\w+(-+.\\w+)*@\\w+(-.\\w+)*.\\w+(-.\\w+)*$";

	@Resource
	private UserMapper userMapper;

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	@Resource
	private RedissonLockUtil redissonLockUtil;

	/**
	 * 用户寄存器
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return long
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public long userRegister(UserRegisterRequest userRegisterRequest) {
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String username = userRegisterRequest.getUsername();
		String checkPassword = userRegisterRequest.getCheckPassword();
		String invitationCode = userRegisterRequest.getInvitationCode();

		// 1. 校验
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "参数为空");
		}
		if (username.length() > 40) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "昵称过长");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户账号过短，不能少于4位");
		}
		if (userPassword.length() < 4 || checkPassword.length() < 4) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户密码过短，不能少于4位");
		}
		//  5. 账号和密码不包含特殊字符
		// 匹配由数字、大小写字母组成的字符串,且字符串的长度至少为1个字符
		String pattern = "[0-9a-zA-Z]+";
		if (!userAccount.matches(pattern)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号由数字和大小写字母组成");
		}
		if (!userPassword.matches(pattern)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码由数字和大小写字母组成");
		}
		// 密码和校验密码相同
		if (!userPassword.equals(checkPassword)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "两次输入的密码不一致");
		}
		String redissonLock = ("userRegister_" + userAccount).intern();
		return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
			// 账户不能重复
			LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(User::getUserAccount, userAccount);
			long count = userMapper.selectCount(queryWrapper);
			if (count > 0) {
				throw new BusinessException(ResponseCode.PARAMS_ERROR, "该账号已被注册");
			}
			User invitationCodeUser = null;
			if (StringUtils.isNotBlank(invitationCode)) {
				LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
				userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
				// 可能出现重复invitationCode,查出的不是一条
				invitationCodeUser = this.getOne(userLambdaQueryWrapper);
				if (invitationCodeUser == null) {
					throw new BusinessException(ResponseCode.OPERATION_ERROR, "该邀请码无效");
				}
			}
			// 2. 加密
			String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
			// ak/sk
			String accessKey = DigestUtils.md5DigestAsHex((userAccount + SALT + VOUCHER).getBytes());
			String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + userAccount).getBytes());

			// 3. 插入数据
			User user = new User();
			user.setUserAccount(userAccount);
			user.setUserPassword(encryptPassword);
			user.setUsername(username);
			user.setAccessKey(accessKey);
			user.setSecretKey(secretKey);
			if (invitationCodeUser != null) {
				user.setBalance(100);
				this.addWalletBalance(invitationCodeUser.getId(), 100);
			}
			user.setInvitationCode(generateRandomString(8));
			boolean saveResult = this.save(user);
			if (!saveResult) {
				throw new BusinessException(ResponseCode.SYSTEM_ERROR, "注册失败，数据库错误");
			}
			return user.getId();
		}, "注册账号失败");
	}


	/**
	 * 用户电子邮件注册
	 *
	 * @param userEmailRegisterRequest 用户电子邮件注册请求
	 * @return long
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public long userEmailRegister(UserEmailRegisterRequest userEmailRegisterRequest) {
		String email = userEmailRegisterRequest.getEmail();
		String captcha = userEmailRegisterRequest.getCaptcha();
		String username = userEmailRegisterRequest.getUsername();
		String invitationCode = userEmailRegisterRequest.getInvitationCode();

		if (StringUtils.isAnyBlank(email, captcha)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		if (username.length() > 40) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "昵称过长");
		}
		if (!Pattern.matches(EMAIL_PATTERN, email)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "不合法的邮箱地址");
		}
		String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + email);
		if (StringUtils.isBlank(cacheCaptcha)) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "验证码已过期,请重新获取");
		}
		captcha = captcha.trim();
		if (!cacheCaptcha.equals(captcha)) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "验证码输入有误");
		}
		String redissonLock = ("userEmailRegister_" + email).intern();
		return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
			// 邮箱不能重复
			LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(User::getEmail, email);
			long count = userMapper.selectCount(queryWrapper);
			if (count > 0) {
				throw new BusinessException(ResponseCode.PARAMS_ERROR, "该邮箱已被注册");
			}
			User invitationCodeUser = null;
			if (StringUtils.isNotBlank(invitationCode)) {
				LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
				userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
				// 可能出现重复invitationCode,查出的不是一条
				invitationCodeUser = this.getOne(userLambdaQueryWrapper);
				if (invitationCodeUser == null) {
					throw new BusinessException(ResponseCode.OPERATION_ERROR, "该邀请码无效");
				}
			}
			// ak/sk
			String accessKey = DigestUtils.md5DigestAsHex((Arrays.toString(RandomUtil.randomBytes(10)) + SALT + VOUCHER).getBytes());
			String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + Arrays.toString(RandomUtil.randomBytes(10))).getBytes());

			// 3. 插入数据
			User user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setAccessKey(accessKey);
			user.setSecretKey(secretKey);
			if (invitationCodeUser != null) {
				user.setBalance(100);
				this.addWalletBalance(invitationCodeUser.getId(), 100);
			}
			user.setInvitationCode(generateRandomString(8));
			boolean saveResult = this.save(user);
			if (!saveResult) {
				throw new BusinessException(ResponseCode.SYSTEM_ERROR, "注册失败，数据库错误");
			}
			return user.getId();
		}, "邮箱账号注册失败");
	}


	/**
	 * 用户登录
	 *
	 * @param userAccount  用户帐户
	 * @param userPassword 用户密码
	 * @param request      要求
	 * @return {@link UserVo}
	 */
	@Override
	public UserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "参数为空");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户账号过短,不能少于4位");
		}
		if (userPassword.length() < 4) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户密码过短,不能少于4位");
		}
		//  5. 账户不包含特殊字符
		// 匹配由数字、字母组成的字符串,且字符串的长度至少为1个字符
		String pattern = "[0-9a-zA-Z]+";
		if (!userAccount.matches(pattern) && !userAccount.matches(EMAIL_PATTERN)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号由数字和大小写字母组成");
		}
		if (!userPassword.matches(pattern)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码由数字和大小写字母组成");
		}
		// 2. 加密
		String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
		// 查询用户是否存在
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(User::getUserAccount, userAccount);
		queryWrapper.eq(User::getUserPassword, encryptPassword);
		User user = userMapper.selectOne(queryWrapper);
		// 用户不存在
		if (user == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户不存在或密码错误");
		}
		if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
			throw new BusinessException(ResponseCode.PROHIBITED, "账号已封禁");
		}
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(user, userVo);
		// 3. 记录用户的登录态
		request.getSession().setAttribute(USER_LOGIN_STATE, userVo);
		return userVo;
	}

	/**
	 * 用户电子邮件登录
	 *
	 * @param userEmailLoginRequest 用户电子邮件登录请求
	 * @param request               要求
	 * @return {@link UserVo}
	 */
	@Override
	public UserVo userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
		String email = userEmailLoginRequest.getEmail();
		String captcha = userEmailLoginRequest.getCaptcha();

		if (StringUtils.isAnyBlank(email, captcha)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		if (!Pattern.matches(EMAIL_PATTERN, email)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "不合法的邮箱地址！");
		}
		String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + email);
		if (StringUtils.isBlank(cacheCaptcha)) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "验证码已过期,请重新获取");
		}
		captcha = captcha.trim();
		if (!cacheCaptcha.equals(captcha)) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "验证码输入有误");
		}
		// 查询用户是否存在
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("email", email);
		User user = userMapper.selectOne(queryWrapper);

		// 用户不存在
		if (user == null) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "该邮箱尚未注册，请前往注册~");
		}

		if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())) {
			throw new BusinessException(ResponseCode.PROHIBITED, "账号已封禁");
		}
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(user, userVo);
		// 3. 记录用户的登录态
		request.getSession().setAttribute(USER_LOGIN_STATE, userVo);
		return userVo;
	}

	@Override
	public UserVo userBindEmail(UserBindEmailRequest userEmailLoginRequest, HttpServletRequest request) {
		String email = userEmailLoginRequest.getEmail();
		String captcha = userEmailLoginRequest.getCaptcha();
		if (StringUtils.isAnyBlank(email, captcha)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		if (!Pattern.matches(EMAIL_PATTERN, email)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "不合法的邮箱地址！");
		}
		String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + email);
		if (StringUtils.isBlank(cacheCaptcha)) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "验证码已过期,请重新获取");
		}
		captcha = captcha.trim();
		if (!cacheCaptcha.equals(captcha)) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "验证码输入有误");
		}
		// 查询用户是否绑定该邮箱
		UserVo loginUser = this.getLoginUser(request);
		if (loginUser.getEmail() != null && email.equals(loginUser.getEmail())) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "该账号已绑定此邮箱,请更换新的邮箱！");
		}
		// 查询邮箱是否已经绑定
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("email", email);
		User user = this.getOne(queryWrapper);
		if (user != null) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "此邮箱已被绑定,请更换新的邮箱！");
		}
		user = new User();
		user.setId(loginUser.getId());
		user.setEmail(email);
		boolean bindEmailResult = this.updateById(user);
		if (!bindEmailResult) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "邮箱绑定失败,请稍后再试！");
		}
		loginUser.setEmail(email);
		return loginUser;
	}

	/**
	 * 获取当前登录用户
	 *
	 * @param request 要求
	 * @return {@link User}
	 */
	@Override
	public UserVo getLoginUser(HttpServletRequest request) {
		// 先判断是否已登录
		UserVo loginUser = (UserVo) request.getSession().getAttribute(USER_LOGIN_STATE);
		if (loginUser == null || loginUser.getId() == null) {
			throw new BusinessException(403, "使用游客身份访问");
		}
		User user = getById(loginUser.getId());
		BeanUtil.copyProperties(user, loginUser);
		return loginUser;
	}

	/**
	 * 是否为管理员
	 *
	 * @param request 要求
	 * @return boolean
	 */
	@Override
	public boolean isAdmin(HttpServletRequest request) {
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		UserVo currentUser = (UserVo) userObj;
		if (currentUser == null || currentUser.getId() == null) {
			return false;
		}
		// 从数据库查询（追求性能的话可以注释，直接走缓存）
		long userId = currentUser.getId();
		User user = this.getById(userId);
		return user != null && ADMIN_ROLE.equals(user.getUserRole());
	}

	@Override
	public User isTourist(HttpServletRequest request) {
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		UserVo currentUser = (UserVo) userObj;
		if (currentUser == null || currentUser.getId() == null) {
			return null;
		}
		// 从数据库查询（追求性能的话可以注释，直接走缓存）
		long userId = currentUser.getId();
		return this.getById(userId);
	}

	/**
	 * 用户注销
	 *
	 * @param request 要求
	 * @return boolean
	 */
	@Override
	public boolean userLogout(HttpServletRequest request) {
		if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "未登录");
		}
		// 移除登录态
		request.getSession().removeAttribute(USER_LOGIN_STATE);
		return true;
	}

	@Override
	public void validUser(User user, UserVo loginUser, boolean add) {
		if (user == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		String userAccount = user.getUserAccount();
		String userPassword = user.getUserPassword();
		Integer balance = user.getBalance();

		// 创建时，所有参数必须非空
		if (add) {
			if (StringUtils.isAnyBlank(userAccount, userPassword)) {
				throw new BusinessException(ResponseCode.PARAMS_ERROR);
			}
			// 添加用户生成8位邀请码
			user.setInvitationCode(generateRandomString(8));
		}

		if (userAccount != null && userAccount.length() < 4) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户账号过短,不能少于4位");
		}
		if (userPassword != null && userPassword.length() < 4) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户密码过短,不能少于4位");
		}
		//  5. 账户不包含特殊字符
		// 匹配由数字、字母组成的字符串,且字符串的长度至少为1个字符
		String pattern = "[0-9a-zA-Z]+";
		if (StringUtils.isNotBlank(userAccount) && !userAccount.matches(pattern)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号由数字和大小写字母组成");
		}
		if (StringUtils.isNotBlank(userPassword) && !userPassword.matches(pattern)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码由数字和大小写字母组成");
		}
		if (ObjectUtils.isNotEmpty(balance) && balance < 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "钱包余额不能为负数");
		}
		if (StringUtils.isNotBlank(userPassword)) {
			String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
			user.setUserPassword(encryptPassword);
		}
		// 账户不能重复
		if (StringUtils.isNotBlank(userAccount)) {
			LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(User::getUserAccount, userAccount);
			long count = userMapper.selectCount(queryWrapper);

			if (count > 0 && !loginUser.getUserAccount().equals(user.getUserAccount())) {
				throw new BusinessException(ResponseCode.PARAMS_ERROR, "该账号已被绑定");
			}
		}
	}

	@Override
	public UserVo updateVoucher(User loginUser) {
		String accessKey = DigestUtils.md5DigestAsHex((Arrays.toString(RandomUtil.randomBytes(10)) + SALT + VOUCHER).getBytes());
		String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + Arrays.toString(RandomUtil.randomBytes(10))).getBytes());
		loginUser.setAccessKey(accessKey);
		loginUser.setSecretKey(secretKey);
		boolean result = this.updateById(loginUser);
		if (!result) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR);
		}
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(loginUser, userVo);
		return userVo;
	}

	@Override
	public boolean addWalletBalance(Long userId, Integer addPoints) {
		LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
		userLambdaUpdateWrapper.eq(User::getId, userId);
		userLambdaUpdateWrapper.setSql("balance = balance + " + addPoints);
		return this.update(userLambdaUpdateWrapper);
	}

	@Override
	public boolean reduceWalletBalance(Long userId, Integer reduceScore) {
		LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
		userLambdaUpdateWrapper.eq(User::getId, userId);
		userLambdaUpdateWrapper.setSql("balance = balance - " + reduceScore);
		return this.update(userLambdaUpdateWrapper);
	}

	/**
	 * 生成随机字符串
	 *
	 * @param length 长
	 * @return {@link String}
	 */
	public String generateRandomString(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder(length);
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(characters.length());
			char randomChar = characters.charAt(index);
			sb.append(randomChar);
		}
		return sb.toString();
	}
}




