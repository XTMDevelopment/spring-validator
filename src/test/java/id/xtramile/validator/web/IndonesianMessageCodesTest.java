package id.xtramile.validator.web;

import id.xtramile.validator.support.LocaleMessageCodesTestSupport;
import static org.assertj.core.api.Assertions.assertThat;

class IndonesianMessageCodesTest extends LocaleMessageCodesTestSupport {

    @Override
    protected String locale() {
        return "id";
    }

    @Override
    protected String resourceName() {
        return "messages_id.properties";
    }

    @Override
    protected void assertFormattedMessages(MessageResourceResolver resolver) {
        assertThat(resolver.getMessage("validation.spring.not-null", "email"))
                .isEqualTo("email wajib diisi");

        assertThat(resolver.getMessage("validation.spring.size", "password", 8, 20))
                .isEqualTo("password harus antara 8 dan 20 karakter");

        assertThat(resolver.getMessage("validation.common.in-whitelist", "status", "aktif,nonaktif"))
                .isEqualTo("status harus salah satu dari: aktif,nonaktif");

        assertThat(resolver.getMessage("validation.data.tax-id.length", "taxId", "15-16"))
                .isEqualTo("taxId harus memiliki 15-16 digit");

        assertThat(resolver.getMessage("validation.datetime.invalid-past-date", "tanggal"))
                .isEqualTo("tanggal harus berupa tanggal yang sama dengan hari ini");

        assertThat(resolver.getMessage("validation.datetime.invalid-future-date", "tanggal"))
                .isEqualTo("tanggal harus berupa tanggal yang sama dengan hari ini");
    }

}
