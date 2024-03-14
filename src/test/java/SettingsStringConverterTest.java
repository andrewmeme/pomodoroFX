import ancientmeme.pomodoro.settings.SettingsStringConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SettingsStringConverterTest {
    private SettingsStringConverter converter;

    @BeforeEach
    public void setup() {
        converter = new SettingsStringConverter();
    }

    @Test
    public void fromStringTest() {
        long normalValue = converter.fromString("42");
        Assertions.assertEquals(42L, normalValue);

        long zeroValue = converter.fromString("0");
        Assertions.assertEquals(1L, zeroValue);

        long negativeValue = converter.fromString("-1");
        Assertions.assertEquals(1L, negativeValue);

        long overSixtyValue = converter.fromString("69");
        Assertions.assertEquals(60L, overSixtyValue);
    }
}
