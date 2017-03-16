package com.example.utils;

import java.util.concurrent.TimeUnit;

public class Timeout {

	private long timeLimitInMilliseconds;
	private long timePointInMilliseconds;
	
	public Timeout(long timeLimit_, TimeUnit timeUnit_, TimeoutStatus timeoutStatus_) {

		timeLimitInMilliseconds = TimeUnit.MILLISECONDS.convert(timeLimit_, timeUnit_);
		if(TimeoutStatus.SET.equals(timeoutStatus_)){
			timePointInMilliseconds = System.currentTimeMillis()-timeLimitInMilliseconds-1;
		}
		else{
			timePointInMilliseconds = System.currentTimeMillis();
		}
	}

	public boolean isSet() {

		long currentTimePointInMilliseconds = System.currentTimeMillis();
		long durationInMilliseconds = currentTimePointInMilliseconds-timePointInMilliseconds;
		if( timeLimitInMilliseconds < durationInMilliseconds )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void reset() {
		timePointInMilliseconds = System.currentTimeMillis();
	}

	public long getTimeLimit(TimeUnit timeUnit_) {
		return timeUnit_.convert(timeLimitInMilliseconds, TimeUnit.MILLISECONDS);
	}

	public void setTimeLimit(long timeLimit_, TimeUnit timeUnit_) {
		timeLimitInMilliseconds = TimeUnit.MILLISECONDS.convert(timeLimit_, timeUnit_);
	}

	
}
