package com.huaqi.elb.lb;

import com.huaqi.common.msg.AppEngine;
import com.huaqi.elb.service.impl.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 轮训
 */
@Component("roundRobin")
public class RoundRobin implements LoadBalance {

	private static Integer pos = 0;

	@Autowired
	private SessionManager sessionManager;

	@Override
	public AppEngine getAppEngineServer() {

		List<AppEngine> appEngineList = sessionManager.getAllServerList();

		AppEngine appEngine = null;
		synchronized(this) {
			if (pos > appEngineList.size()) {
				pos = 0;
			}

			appEngine = appEngineList.get(pos);
			pos ++;
		}
		
		return appEngine;
	}
}