package com.virtualpairprogrammers.tracker.messaging;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.virtualpairprogrammers.tracker.data.Data;
import com.virtualpairprogrammers.tracker.domain.VehicleBuilder;
import com.virtualpairprogrammers.tracker.domain.VehiclePosition;

@Component
public class MessageProcessor {
	
	@Autowired
	private Data data;
	
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	@JmsListener(destination="${fleetman.position.queue}")
	public void processPositionMessageFromQueue(Map<String, String> incomingMessage ) throws ParseException 
	{
		String positionDatestamp = incomingMessage.get("time");
		Date convertedDatestamp = format.parse(positionDatestamp);
		
		VehiclePosition newReport = new VehicleBuilder()
				                          .withName(incomingMessage.get("vehicle"))
				                          .withLat(new BigDecimal(incomingMessage.get("lat")))
				                          .withLng(new BigDecimal(incomingMessage.get("long")))
				                          .withTimestamp(convertedDatestamp)
				                          .withSpeed(getRandomVehicleSpeed(20, 150))
				                          .build();
				                          
		data.updatePosition(newReport);
	}

	public static BigDecimal getRandomVehicleSpeed(int min, int max) {
	        Random random = new Random();
	        double speedValue = min + (max - min) * random.nextDouble();
	        return BigDecimal.valueOf(speedValue).setScale(2, RoundingMode.HALF_UP);
    	}

}
