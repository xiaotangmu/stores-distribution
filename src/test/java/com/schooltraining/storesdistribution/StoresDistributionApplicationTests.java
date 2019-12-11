package com.schooltraining.storesdistribution;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class StoresDistributionApplicationTests {

	@Test
	void contextLoads() throws ParseException {
		String str = "2019-07-11 13:33:23";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(str);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		System.out.println(calendar.get(Calendar.YEAR));
		if(calendar.get(Calendar.YEAR) == 2019){
			System.out.println(calendar.get(Calendar.MONTH) + 1);//月份从0开始，所以要加一
		}

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(new Date());
		//获取当前年
		int yearStr = calendar2.get(Calendar.YEAR);
		System.out.println(yearStr);
		//获取当前月
		int monStr = calendar2.get(Calendar.MONTH) + 1;
		System.out.println(monStr);
	}

}
