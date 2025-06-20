package org.c4marathon.assignment.infra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Mini-pay API", description = """
                    기본 송금과 정산 기능이 있는 페이 서비스
                """, version = "v1", contact = @Contact(name = "조시현", email = "si4018@naver.com", url = "https://github.com/sihyunjojo/c4-cometrue-assignment/tree/faeture/mini-pay/main")), servers = {
                @Server(url = "http://localhost:8080", description = "로컬 개발 서버")
})
public class SwaggerConfiguration {

}
