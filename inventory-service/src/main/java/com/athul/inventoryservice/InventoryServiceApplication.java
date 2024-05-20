package com.athul.inventoryservice;

import com.athul.inventoryservice.model.Inventory;
import com.athul.inventoryservice.repo.InventoryRepo;
import com.netflix.discovery.EurekaNamespace;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepo inventoryRepo, HttpServletRequest request){
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iPhone_13");
			inventory.setQuantity(100);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("iPhone_13_red");
			inventory1.setQuantity(0);

			inventoryRepo.save(inventory);
			inventoryRepo.save(inventory1);

		};
	}
}

/*comment*/

