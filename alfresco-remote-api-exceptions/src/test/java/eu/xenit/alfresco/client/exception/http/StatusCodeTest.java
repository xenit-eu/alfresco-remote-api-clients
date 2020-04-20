package eu.xenit.alfresco.client.exception.http;

import static org.assertj.core.api.Assertions.assertThat;

import eu.xenit.alfresco.client.exception.StatusCode;
import org.junit.jupiter.api.Test;

class StatusCodeTest {

    @Test
    void is1xxInformational() {
        assertThat(StatusCode.CONTINUE.is1xxInformational()).isTrue();
        assertThat(StatusCode.OK.is1xxInformational()).isFalse();
        assertThat(StatusCode.MULTIPLE_CHOICES.is1xxInformational()).isFalse();
        assertThat(StatusCode.BAD_REQUEST.is1xxInformational()).isFalse();
        assertThat(StatusCode.INTERNAL_SERVER_ERROR.is1xxInformational()).isFalse();
    }

    @Test
    void is2xxSuccessful() {
        assertThat(StatusCode.CONTINUE.is2xxSuccessful()).isFalse();
        assertThat(StatusCode.OK.is2xxSuccessful()).isTrue();
        assertThat(StatusCode.MULTIPLE_CHOICES.is2xxSuccessful()).isFalse();
        assertThat(StatusCode.BAD_REQUEST.is2xxSuccessful()).isFalse();
        assertThat(StatusCode.INTERNAL_SERVER_ERROR.is2xxSuccessful()).isFalse();
    }

    @Test
    void is3xxRedirection() {
        assertThat(StatusCode.CONTINUE.is3xxRedirection()).isFalse();
        assertThat(StatusCode.OK.is3xxRedirection()).isFalse();
        assertThat(StatusCode.MULTIPLE_CHOICES.is3xxRedirection()).isTrue();
        assertThat(StatusCode.BAD_REQUEST.is3xxRedirection()).isFalse();
        assertThat(StatusCode.INTERNAL_SERVER_ERROR.is3xxRedirection()).isFalse();
    }

    @Test
    void is4xxClientError() {
        assertThat(StatusCode.CONTINUE.is4xxClientError()).isFalse();
        assertThat(StatusCode.OK.is4xxClientError()).isFalse();
        assertThat(StatusCode.MULTIPLE_CHOICES.is4xxClientError()).isFalse();
        assertThat(StatusCode.BAD_REQUEST.is4xxClientError()).isTrue();
        assertThat(StatusCode.INTERNAL_SERVER_ERROR.is4xxClientError()).isFalse();
    }

    @Test
    void is5xxServerError() {
        assertThat(StatusCode.CONTINUE.is5xxServerError()).isFalse();
        assertThat(StatusCode.OK.is5xxServerError()).isFalse();
        assertThat(StatusCode.MULTIPLE_CHOICES.is5xxServerError()).isFalse();
        assertThat(StatusCode.BAD_REQUEST.is5xxServerError()).isFalse();
        assertThat(StatusCode.INTERNAL_SERVER_ERROR.is5xxServerError()).isTrue();
    }


}