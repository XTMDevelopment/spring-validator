package id.xtramile.validator.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ISOTypeTest {

    @Test
    void values_returnsAllConstantsInOrder() {
        assertArrayEquals(
                new ISOType[]{ISOType.CURRENCY, ISOType.COUNTRY_ALPHA2, ISOType.COUNTRY_ALPHA3, ISOType.LANGUAGE},
                ISOType.values()
        );
    }

    @Test
    void valueOf_resolvesKnownNames() {
        assertEquals(ISOType.CURRENCY, ISOType.valueOf("CURRENCY"));
        assertEquals(ISOType.COUNTRY_ALPHA2, ISOType.valueOf("COUNTRY_ALPHA2"));
        assertEquals(ISOType.COUNTRY_ALPHA3, ISOType.valueOf("COUNTRY_ALPHA3"));
        assertEquals(ISOType.LANGUAGE, ISOType.valueOf("LANGUAGE"));
    }

    @Test
    void valueOf_rejectsInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ISOType.valueOf("INVALID_ISO_TYPE"));
    }

    @Test
    void valueOf_rejectsNull() {
        assertThrows(NullPointerException.class, () -> ISOType.valueOf(null));
    }
}
