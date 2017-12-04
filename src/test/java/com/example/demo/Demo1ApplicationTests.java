package com.example.demo;

import com.example.demo.bean.Car;
import com.example.demo.bean.CarVO;
import com.example.demo.repository.PersonDataRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class Demo1ApplicationTests {

	@Autowired
	private PersonDataRepository personDataRepository;

	@Test
	public void testAdd() {
		//应用插入的数据被回滚
		personDataRepository.addCarAndHouse(true);
	}

	@Test
	public void testQuery() {

		System.out.println("=======查询所有的");
		Object carObj = personDataRepository.queryFor().get("car");
		Object houseObj = personDataRepository.queryFor().get("house");

		List carList = Arrays.asList(carObj);
		List houseList = Arrays.asList(houseObj);
		carList.forEach(System.out::println);
		houseList.forEach(System.out::println);
	}

	@Test
	public void testCount() {
		for (int i = 0; i < 100; i++){
			personDataRepository.addCar(new Car("制造商"+ i,"型号" + (i + 1), i+1,new Long(i)));
		}
		Collection<CarVO> carVOS = personDataRepository.queryCount();
		org.springframework.util.Assert.isTrue(carVOS.size()==1,"应该是一个元素");
	}
}
