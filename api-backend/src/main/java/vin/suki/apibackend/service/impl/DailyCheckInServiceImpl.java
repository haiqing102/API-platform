package vin.suki.apibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import vin.suki.apibackend.mapper.DailyCheckInMapper;
import vin.suki.apibackend.service.DailyCheckInService;
import vin.suki.apicommon.model.entity.DailyCheckIn;
import org.springframework.stereotype.Service;

@Service
public class DailyCheckInServiceImpl extends ServiceImpl<DailyCheckInMapper, DailyCheckIn>
		implements DailyCheckInService {

}




