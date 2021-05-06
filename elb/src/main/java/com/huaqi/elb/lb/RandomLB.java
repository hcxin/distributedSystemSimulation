package com.huaqi.elb.lb;

import com.huaqi.common.msg.AppEngine;
import com.huaqi.elb.service.impl.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
@Component("randomLB")
public class RandomLB implements LoadBalance {

	@Autowired
	private SessionManager sessionManager;
	@Override
	public AppEngine getAppEngineServer() {
		List<AppEngine> appEngineList = sessionManager.getAllServerList();
		
		Random random = new Random();
		int randomPos = random.nextInt(appEngineList.size());
		
		return appEngineList.get(randomPos);
	}

}