package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class ApiTest {

	@GetMapping("/api/volunteer/search-words")
	@ResponseBody
	public String getSearchWords() {

		String url = "http://openapi.1365.go.kr/openapi/service/rest/VolunteerPartcptnService/getVltrSearchWordList"
				+ "?serviceKey=aKsBJu3J7F8zaO41O0cV7X9Z2h4cCsLur72T4evzMXhYL49Ga9CkiyxGtkMmUPDvxMBrgNu305vyXK2Zt8SxAg%3D%3D" + "&numOfRows=10" + "&pageNo=1";

		RestTemplate rt = new RestTemplate();
		return rt.getForObject(url, String.class);
	}

}
