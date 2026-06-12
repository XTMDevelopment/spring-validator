package id.xtramile.validator.web;

import id.xtramile.validator.support.LocaleMessageCodesTestSupport;

import java.util.List;

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
    protected List<FormattedMessageCase> formattedMessageCases() {
        return List.of(
                new FormattedMessageCase("validation.spring.not-null", new Object[]{"email"}, "email wajib diisi"),
                new FormattedMessageCase("validation.spring.size", new Object[]{"password", 8, 20},
                        "password harus antara 8 dan 20 karakter"),
                new FormattedMessageCase("validation.common.in-whitelist", new Object[]{"status", "aktif,nonaktif"},
                        "status harus salah satu dari: aktif,nonaktif"),
                new FormattedMessageCase("validation.data.tax-id.length", new Object[]{"taxId", "15-16"},
                        "taxId harus memiliki 15-16 digit"),
                new FormattedMessageCase("validation.datetime.invalid-past-date", new Object[]{"tanggal"},
                        "tanggal harus berupa tanggal yang sama dengan hari ini"),
                new FormattedMessageCase("validation.datetime.invalid-future-date", new Object[]{"tanggal"},
                        "tanggal harus berupa tanggal yang sama dengan hari ini")
        );
    }
}
