package org.c4marathon.assignment.api.dlq;

import java.util.Map;

import org.c4marathon.assignment.usecase.dlq.DlqUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "DLQ API", description = "DLQ 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dlq")
public class DlqController {

    private final DlqUsecase dlqUsecase;

    @Operation(summary = "DLQ 메시지 수 조회", description = "지정된 DLQ의 메시지 수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "DLQ 메시지 수 조회 성공",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/messages/count")
    public ResponseEntity<org.c4marathon.assignment.response.ApiResponse<Long>> getDlqMessageCount() {
        long messageCount = dlqUsecase.getMQSize();

		return ResponseEntity.ok(
			org.c4marathon.assignment.response.ApiResponse.res(200, "DLQ 메시지 수 조회 성공", messageCount));
    }
}
