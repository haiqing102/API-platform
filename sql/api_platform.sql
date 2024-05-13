/*
 Navicat Premium Data Transfer

 Source Server         : @43.138.216.214
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : 43.138.216.214:3306
 Source Schema         : api_platform

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 13/05/2024 12:32:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for daily_check_in
-- ----------------------------
DROP TABLE IF EXISTS `daily_check_in`;
CREATE TABLE `daily_check_in`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) NOT NULL COMMENT '签到人',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `add_points` bigint(20) NOT NULL DEFAULT 10 COMMENT '签到增加积分个数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1789819790213697538 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日签到表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口名称',
  `url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口地址',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '发布人',
  `method` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方法',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接口请求参数',
  `response_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接口响应参数',
  `reduce_score` bigint(20) NULL DEFAULT 0 COMMENT '扣除积分数',
  `request_example` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求示例',
  `request_header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求头',
  `response_header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应头',
  `return_format` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'JSON' COMMENT '返回格式(JSON等等)',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述信息',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '接口状态（0- 默认下线 1- 上线）',
  `total_invokes` bigint(20) NOT NULL DEFAULT 0 COMMENT '接口总调用次数',
  `avatar_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口头像',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1785418263822745603 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '接口信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interface_info
-- ----------------------------
INSERT INTO `interface_info` VALUES (1705234447153963010, '幽默段子', 'https://api.suki.vin/api/interface/poisonousChickenSoup', 1698354419367571457, 'GET', NULL, '[{\"id\":\"1695051685885\",\"fieldName\":\"code\",\"type\":\"int\",\"desc\":\"响应码\"},{\"id\":\"1695052930602\",\"fieldName\":\"data.text\",\"type\":\"string\",\"desc\":\"幽默段子\"},{\"id\":\"1695052955781\",\"fieldName\":\"message\",\"type\":\"string\",\"desc\":\"响应描述\"}]', 1, 'https://api.suki.vin/api/interface/poisonousChickenSoup', NULL, NULL, 'JSON', '只为博君一笑', 1, 83, 'https://img.suki.vin/interface_icon/huixieyulu.png', '2023-09-22 22:55:48', '2024-05-11 20:02:19', 0);
INSERT INTO `interface_info` VALUES (1705237104270712833, '获取输入的名称', 'https://api.suki.vin/api/interface/name', 1698354419367571457, 'GET', '[{\"id\":\"1695031845159\",\"fieldName\":\"name\",\"type\":\"string\",\"desc\":\"输入的名称\",\"required\":\"是\"}]', '[{\"id\":\"1695105888173\",\"fieldName\":\"data.name\",\"type\":\"object\",\"desc\":\"输入的参数\"},{\"id\":\"1695382937817\",\"fieldName\":\"code\",\"type\":\"int\",\"desc\":\"响应码\"},{\"id\":\"1695382949291\",\"fieldName\":\"message\",\"type\":\"string\",\"desc\":\"响应信息描述\"}]', 1, 'https://api.suki.vin/api/interface/name', NULL, NULL, 'JSON', '获取输入的名称', 2, 2, 'https://img.suki.vin/interface_icon/name.png', '2023-09-22 23:06:22', '2024-05-02 17:44:42', 1);
INSERT INTO `interface_info` VALUES (1705237990061580289, '随机壁纸', 'https://api.suki.vin/api/interface/randomWallpaper', 1698354419367571457, 'GET', '[{\"id\":\"1695032007961\",\"fieldName\":\"method\",\"type\":\"string\",\"desc\":\"壁纸输出端：[ pc | mobile | zsy ]，默认为pc端\",\"required\":\"否\"},{\"id\":\"1695032018924\",\"fieldName\":\"lx\",\"type\":\"string\",\"desc\":\"壁纸类型：[ fengjing | dongman | meizi | suiji ]，默认为风景\",\"required\":\"否\"}]', '[{\"id\":\"1695051751595\",\"fieldName\":\"code\",\"type\":\"string\",\"desc\":\"响应码\"},{\"id\":\"1695051832571\",\"fieldName\":\"data.imgurl\",\"type\":\"string\",\"desc\":\"返回的壁纸地址\"},{\"id\":\"1695051861456\",\"fieldName\":\"message\",\"type\":\"string\",\"desc\":\"响应消息\"}]', 1, 'https://api.suki.vin/api/interface/randomWallpaper', NULL, NULL, 'JSON', '获取随机壁纸', 1, 99, 'https://img.suki.vin/interface_icon/bizhi.png', '2023-09-22 23:09:53', '2024-05-11 12:36:50', 0);
INSERT INTO `interface_info` VALUES (1705238841173942274, '星座运势', 'https://api.suki.vin/api/interface/horoscope', 1698354419367571457, 'GET', '[{\"id\":\"1695382662346\",\"fieldName\":\"type\",\"type\":\"string\",\"desc\":\"十二星座：aries(白羊), taurus(金牛), gemini(双子), cancer(巨蟹), leo(狮子), virgo(处女), libra(天秤), scorpio(天蝎), sagittarius(射手), capricorn(摩羯), aquarius(水瓶), pisces(双鱼)\",\"required\":\"是\"},{\"id\":\"1695382692472\",\"fieldName\":\"time\",\"type\":\"string\",\"desc\":\"时间：today(今天), nextday(明天), week(本周), month(本月), year(本年)\",\"required\":\"是\"}]', '[{\"id\":\"1695382701088\",\"fieldName\":\"code\",\"type\":\"int\",\"desc\":\"响应码\"},{\"id\":\"1695382720309\",\"fieldName\":\"todo.yi\",\"type\":\"string\",\"desc\":\"宜做\"},{\"id\":\"1695382741895\",\"fieldName\":\"todo.ji\",\"type\":\"string\",\"desc\":\"忌做\"},{\"id\":\"1695382783855\",\"fieldName\":\"fortunetext.all\",\"type\":\"string\",\"desc\":\"整体运势\"},{\"id\":\"1695382810906\",\"fieldName\":\"fortunetext.love\",\"type\":\"string\",\"desc\":\"爱情运势\"},{\"id\":\"1695382836727\",\"fieldName\":\"fortunetext.work\",\"type\":\"string\",\"desc\":\"工作运势\"},{\"id\":\"1695382864966\",\"fieldName\":\"fortunetext.money\",\"type\":\"string\",\"desc\":\"财运运势\"},{\"id\":\"1695382888169\",\"fieldName\":\"fortunetext.health\",\"type\":\"string\",\"desc\":\"健康运势\"},{\"id\":\"1695382912920\",\"fieldName\":\"fortune.all\",\"type\":\"int\",\"desc\":\"整体运势评分\"},{\"id\":\"1695382931057\",\"fieldName\":\"fortune.love\",\"type\":\"int\",\"desc\":\"爱情运势评分\"},{\"id\":\"1695382952540\",\"fieldName\":\"fortune.work\",\"type\":\"int\",\"desc\":\"工作运势评分\"},{\"id\":\"1695382975321\",\"fieldName\":\"fortune.money\",\"type\":\"int\",\"desc\":\"财运运势评分\"},{\"id\":\"1695382999222\",\"fieldName\":\"fortune.health\",\"type\":\"int\",\"desc\":\"健康运势评分\"},{\"id\":\"1695383027074\",\"fieldName\":\"shortcomment\",\"type\":\"string\",\"desc\":\"简评\"},{\"id\":\"1695383057554\",\"fieldName\":\"luckycolor\",\"type\":\"string\",\"desc\":\"幸运颜色\"},{\"id\":\"1695383079261\",\"fieldName\":\"index.all\",\"type\":\"string\",\"desc\":\"整体指数\"},{\"id\":\"1695383102324\",\"fieldName\":\"index.love\",\"type\":\"string\",\"desc\":\"爱情指数\"},{\"id\":\"1695383125487\",\"fieldName\":\"index.work\",\"type\":\"string\",\"desc\":\"工作指数\"},{\"id\":\"1695383149310\",\"fieldName\":\"index.money\",\"type\":\"string\",\"desc\":\"财运指数\"},{\"id\":\"1695383173441\",\"fieldName\":\"index.health\",\"type\":\"string\",\"desc\":\"健康指数\"},{\"id\":\"1695383203920\",\"fieldName\":\"luckynumber\",\"type\":\"string\",\"desc\":\"幸运数字\"},{\"id\":\"1695383227323\",\"fieldName\":\"time\",\"type\":\"string\",\"desc\":\"日期\"},{\"id\":\"1695383247152\",\"fieldName\":\"title\",\"type\":\"string\",\"desc\":\"星座名称\"},{\"id\":\"1695383269015\",\"fieldName\":\"type\",\"type\":\"string\",\"desc\":\"运势类型\"},{\"id\":\"1695383292088\",\"fieldName\":\"luckyconstellation\",\"type\":\"string\",\"desc\":\"幸运星座\"},{\"id\":\"1695383314942\",\"fieldName\":\"message\",\"type\":\"string\",\"desc\":\"响应描述\"}]', 1, 'https://api.suki.vin/api/interface/horoscope', '', NULL, 'JSON', '看看你今日的运势吧', 1, 50, 'https://img.suki.vin/interface_icon/xingzuo.png', '2023-09-22 23:13:16', '2024-05-10 20:50:20', 0);
INSERT INTO `interface_info` VALUES (1705239469589733378, '土味情话', 'https://api.suki.vin/api/interface/loveTalk', 1698354419367571457, 'GET', NULL, '[{\"id\":\"1695382126371\",\"fieldName\":\"code\",\"type\":\"int\",\"desc\":\"响应码\"},{\"id\":\"1695382173816\",\"fieldName\":\"data.value\",\"type\":\"string\",\"desc\":\"随机土味情话\"},{\"id\":\"1695382205869\",\"fieldName\":\"message\",\"type\":\"string\",\"desc\":\"返回信息描述\"}]', 1, 'https://api.suki.vin/api/interface/loveTalk', NULL, NULL, 'JSON', '爱你在心口难开', 1, 125, 'https://img.suki.vin/interface_icon/love.png', '2023-09-22 23:15:46', '2024-05-11 13:58:18', 0);
INSERT INTO `interface_info` VALUES (1705239928861827073, 'IP归属地', 'https://api.suki.vin/api/interface/ipInfo', 1698354419367571457, 'GET', '[{\"id\":\"1695388304868\",\"fieldName\":\"ip\",\"type\":\"string\",\"desc\":\"输入IP地址\",\"required\":\"是\"}]', '[{\"id\":\"1695382701088\",\"fieldName\":\"code\",\"type\":\"int\",\"desc\":\"响应码\"},{\"id\":\"1695382720309\",\"fieldName\":\"data.ip\",\"type\":\"string\",\"desc\":\"IP地址\"},{\"id\":\"1695382741895\",\"fieldName\":\"data.info.country\",\"type\":\"string\",\"desc\":\"国家\"},{\"id\":\"1695382783855\",\"fieldName\":\"data.info.prov\",\"type\":\"string\",\"desc\":\"省份\"},{\"id\":\"1695382810906\",\"fieldName\":\"data.info.city\",\"type\":\"string\",\"desc\":\"城市\"},{\"id\":\"1695382836727\",\"fieldName\":\"data.info.lsp\",\"type\":\"string\",\"desc\":\"运营商\"},{\"id\":\"1695382864966\",\"fieldName\":\"message\",\"type\":\"string\",\"desc\":\"响应描述\"}]', 1, 'https://api.suki.vin/api/interface/ipInfo', NULL, NULL, 'JSON', '获取IP归属地信息', 1, 39, 'https://img.suki.vin/interface_icon/ip_info.png', '2023-09-22 23:17:35', '2024-05-11 13:46:26', 0);
INSERT INTO `interface_info` VALUES (1705240565347459073, '天气查询', 'https://api.suki.vin/api/interface/weather', 1698354419367571457, 'GET', '[{\"id\":\"1695392098947\",\"fieldName\":\"city\",\"type\":\"string\",\"desc\":\"输入城市（地级市或直辖市）\",\"required\":\"否\"},{\"id\":\"1695392118560\",\"fieldName\":\"ip\",\"type\":\"string\",\"desc\":\"输入IP\",\"required\":\"否\"},{\"id\":\"1695392127763\",\"fieldName\":\"type\",\"type\":\"string\",\"desc\":\"输入week查询一周天气，默认一天\",\"required\":\"否\"}]', '[{\"id\":\"1695382701088\",\"fieldName\":\"code\",\"type\":\"int\",\"desc\":\"响应码\"},{\"id\":\"1695382720309\",\"fieldName\":\"city\",\"type\":\"string\",\"desc\":\"城市\"},{\"id\":\"1695382741895\",\"fieldName\":\"date\",\"type\":\"string\",\"desc\":\"日期\"},{\"id\":\"1695382783855\",\"fieldName\":\"week\",\"type\":\"string\",\"desc\":\"星期几\"},{\"id\":\"1695382810906\",\"fieldName\":\"type\",\"type\":\"string\",\"desc\":\"天气类型\"},{\"id\":\"1695382836727\",\"fieldName\":\"low\",\"type\":\"string\",\"desc\":\"最低温度\"},{\"id\":\"1695382864966\",\"fieldName\":\"high\",\"type\":\"string\",\"desc\":\"最高温度\"},{\"id\":\"1695382892007\",\"fieldName\":\"fengxiang\",\"type\":\"string\",\"desc\":\"风向\"},{\"id\":\"1695382918740\",\"fieldName\":\"fengli\",\"type\":\"string\",\"desc\":\"风力\"},{\"id\":\"1695382951685\",\"fieldName\":\"night.type\",\"type\":\"string\",\"desc\":\"夜间天气类型\"},{\"id\":\"1695382977506\",\"fieldName\":\"night.fengxiang\",\"type\":\"string\",\"desc\":\"夜间风向\"},{\"id\":\"1695383004749\",\"fieldName\":\"night.fengli\",\"type\":\"string\",\"desc\":\"夜间风力\"},{\"id\":\"1695383032988\",\"fieldName\":\"air.aqi\",\"type\":\"int\",\"desc\":\"空气质量指数\"},{\"id\":\"1695383052210\",\"fieldName\":\"air.aqi_level\",\"type\":\"int\",\"desc\":\"空气质量指数级别\"},{\"id\":\"1695383072789\",\"fieldName\":\"air.aqi_name\",\"type\":\"string\",\"desc\":\"空气质量指数名称\"},{\"id\":\"1695383098609\",\"fieldName\":\"air.co\",\"type\":\"string\",\"desc\":\"一氧化碳浓度\"},{\"id\":\"1695383124245\",\"fieldName\":\"air.no2\",\"type\":\"string\",\"desc\":\"二氧化氮浓度\"},{\"id\":\"1695383149267\",\"fieldName\":\"air.o3\",\"type\":\"string\",\"desc\":\"臭氧浓度\"},{\"id\":\"1695383173716\",\"fieldName\":\"air.pm10\",\"type\":\"string\",\"desc\":\"PM10浓度\"},{\"id\":\"1695383198557\",\"fieldName\":\"air.pm25\",\"type\":\"string\",\"desc\":\"PM2.5浓度\"},{\"id\":\"1695383222398\",\"fieldName\":\"air.so2\",\"type\":\"string\",\"desc\":\"二氧化硫浓度\"},{\"id\":\"1695383249835\",\"fieldName\":\"tip\",\"type\":\"string\",\"desc\":\"提示信息\"},{\"id\":\"1695383275472\",\"fieldName\":\"message\",\"type\":\"string\",\"desc\":\"响应描述\"}]', 1, 'https://api.suki.vin/api/interface/weather', NULL, NULL, 'JSON', '获取每日的天气信息', 1, 108, 'https://img.suki.vin/interface_icon/weather.png', '2023-09-22 23:20:07', '2024-05-11 20:04:51', 0);
INSERT INTO `interface_info` VALUES (1785417203737575425, '网易云Hot', 'https://api.suki.vin/api/interface/wyHotMusic', 1780561892755771394, 'GET', NULL, NULL, 2, 'https://api.suki.vin/api/interface/wyHotMusic', NULL, NULL, 'JSON', '获取网易云热榜歌曲', 1, 493, 'https://img.suki.vin/interface_icon/wymusic.png', '2024-05-01 05:13:27', '2024-05-13 11:55:58', 0);
INSERT INTO `interface_info` VALUES (1785417696081756161, '动漫头像', 'https://api.suki.vin/api/interface/cartoonAvatar', 1780561892755771394, 'GET', NULL, NULL, 1, 'https://api.suki.vin/api/interface/cartoonAvatar', NULL, NULL, 'JSON', '获取动漫头像', 1, 237, 'https://img.suki.vin/interface_icon/dongman.png', '2024-05-01 05:15:24', '2024-05-13 11:55:49', 0);
INSERT INTO `interface_info` VALUES (1785418020532142082, '手机号归属地', 'https://api.suki.vin/api/interface/phoneInfo', 1780561892755771394, 'GET', '[{\"id\":\"1714512494552\",\"fieldName\":\"phone\",\"type\":\"string\",\"desc\":\"手机号码\",\"required\":\"是\"}]', NULL, 1, 'https://api.suki.vin/api/interface/phoneInfo', NULL, NULL, 'JSON', '获取手机号归属地信息', 1, 39, 'https://img.suki.vin/interface_icon/号码归属地.png', '2024-05-01 05:16:42', '2024-05-11 20:05:40', 0);
INSERT INTO `interface_info` VALUES (1785418263822745602, '职场人日历', 'https://api.suki.vin/api/interface/calendar', 1780561892755771394, 'GET', NULL, NULL, 1, 'https://api.suki.vin/api/interface/calendar', NULL, NULL, 'JSON', '有趣的职场人专属日历', 1, 42, 'https://img.suki.vin/interface_icon/日历.png', '2024-05-01 05:17:40', '2024-05-11 20:05:14', 0);

-- ----------------------------
-- Table structure for payment_info
-- ----------------------------
DROP TABLE IF EXISTS `payment_info`;
CREATE TABLE `payment_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_no` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户订单号',
  `transaction_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信支付订单号',
  `trade_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易类型',
  `trade_state` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）\n                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
  `trade_state_desc` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易状态描述',
  `success_time` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付完成时间',
  `openid` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户标识',
  `payer_total` bigint(20) NULL DEFAULT NULL COMMENT '用户支付金额',
  `currency` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'CNY' COMMENT '货币类型',
  `payer_currency` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'CNY' COMMENT '用户支付币种',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接口返回内容',
  `total` bigint(20) NULL DEFAULT NULL COMMENT '总金额(分)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1789866212166389763 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '付款信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_info
-- ----------------------------
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品名称',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品描述',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `total` bigint(20) NULL DEFAULT NULL COMMENT '金额(分)',
  `add_points` bigint(20) NOT NULL DEFAULT 0 COMMENT '增加积分个数',
  `product_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'RECHARGE' COMMENT '产品类型（VIP-会员 RECHARGE-充值,RECHARGEACTIVITY-充值活动）',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '商品状态（0- 默认下线 1- 上线）',
  `expiration_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1697087470134259714 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_info
-- ----------------------------
INSERT INTO `product_info` VALUES (1695338876708544514, '100积分', '增加100积分到钱包', 1691069533871013889, 1, 100, 'RECHARGE_ACTIVITY', 1, NULL, '2023-08-26 15:34:20', '2023-08-28 12:58:30', 0);
INSERT INTO `product_info` VALUES (1695773972037839073, '666积分', '增加666积分到钱包', 1691069533871013889, 399, 666, 'RECHARGE', 1, '2023-08-28 13:01:34', '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO `product_info` VALUES (1695776766919888897, '100积分', '增加100积分到钱包', 1691069533871013889, 99, 100, 'RECHARGE', 1, NULL, '2023-08-27 20:34:21', '2023-08-27 20:34:21', 0);
INSERT INTO `product_info` VALUES (1695777072030339073, '300积分', '增加300积分到钱包', 1691069533871013889, 199, 300, 'RECHARGE', 1, NULL, '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO `product_info` VALUES (1695777203236556802, '1000积分', '增加1000积分到钱包', 1691069533871013889, 599, 1000, 'RECHARGE', 1, NULL, '2023-08-27 20:36:05', '2023-08-28 13:02:25', 0);
INSERT INTO `product_info` VALUES (1695778320091631617, '18999积分', '增加18999积分到钱包', 1691069533871013889, 999, 18999, 'RECHARGE', 1, NULL, '2023-08-27 20:40:32', '2023-08-28 13:02:42', 1);
INSERT INTO `product_info` VALUES (1697087470134259713, '10积分', '每日签到获取10积分', 1692848556158709762, 0, 10, 'RECHARGE', 0, NULL, '2023-08-31 11:22:37', '2023-08-31 11:22:37', 1);

-- ----------------------------
-- Table structure for product_order
-- ----------------------------
DROP TABLE IF EXISTS `product_order`;
CREATE TABLE `product_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_no` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `code_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '二维码地址',
  `user_id` bigint(20) NOT NULL COMMENT '创建人',
  `product_id` bigint(20) NOT NULL COMMENT '商品id',
  `order_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `total` bigint(20) NOT NULL COMMENT '金额(分)',
  `status` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NOTPAY' COMMENT '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）\r\n                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
  `pay_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'WX' COMMENT '支付方式（默认 WX- 微信 ZFB- 支付宝）',
  `product_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品信息',
  `form_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '支付宝formData',
  `add_points` bigint(20) NOT NULL DEFAULT 0 COMMENT '增加积分个数',
  `expiration_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1789873816544399363 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for recharge_activity
-- ----------------------------
DROP TABLE IF EXISTS `recharge_activity`;
CREATE TABLE `recharge_activity`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `product_id` bigint(20) NOT NULL COMMENT '商品id',
  `order_no` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户订单号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1789866212216721410 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_account` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `user_avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `email` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别 0-男 1-女',
  `user_role` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
  `user_password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `access_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'accessKey',
  `secret_key` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'secretKey',
  `balance` bigint(20) NOT NULL DEFAULT 30 COMMENT '钱包余额,注册送30币',
  `invitation_code` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邀请码',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '账号状态（0- 正常 1- 封号）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_userAccount`(`user_account`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1789572708567535619 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_interface_invoke
-- ----------------------------
DROP TABLE IF EXISTS `user_interface_invoke`;
CREATE TABLE `user_interface_invoke`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) NOT NULL COMMENT '调用人id',
  `interface_id` bigint(20) NOT NULL COMMENT '接口id',
  `total_invokes` bigint(20) NOT NULL DEFAULT 0 COMMENT '总调用次数',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '调用状态（0- 正常 1- 封号）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1789552475224068099 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户接口调用表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
