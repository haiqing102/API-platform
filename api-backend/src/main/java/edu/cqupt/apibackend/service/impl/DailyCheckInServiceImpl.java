package edu.cqupt.apibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.apibackend.mapper.DailyCheckInMapper;
import edu.cqupt.apibackend.service.DailyCheckInService;
import edu.cqupt.apicommon.model.entity.DailyCheckIn;
import org.springframework.stereotype.Service;

@Service
public class DailyCheckInServiceImpl extends ServiceImpl<DailyCheckInMapper, DailyCheckIn>
		implements DailyCheckInService {

}




