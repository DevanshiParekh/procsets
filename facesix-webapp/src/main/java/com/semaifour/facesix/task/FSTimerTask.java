package com.semaifour.facesix.task;

import java.util.TimerTask;

import com.semaifour.facesix.domain.JSONMap;

public abstract class FSTimerTask extends TimerTask {
		public abstract void setParameters(JSONMap configMap);
}
