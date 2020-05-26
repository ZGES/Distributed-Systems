package ice.sensors;

import sr.ice.gen.Fridge.Reading;
import sr.ice.gen.Fridge.FridgeControl;
import sr.ice.gen.Fridge.Level;
import sr.ice.gen.Fridge.UnusporrtedTypeException;
import com.zeroc.Ice.Current;

import java.text.DecimalFormat;

public class FridgeControlI extends BasicSensor implements FridgeControl {

    private final DecimalFormat formatter = new DecimalFormat("#.##");

    @Override
    public Reading checkTemp(Level freezeLevel, Current current) throws UnusporrtedTypeException {
        switch (freezeLevel) {
            case LOW:
                return new Reading(toHumanSize(random.nextFloat() * 10), freezeLevel);
            case MEDIUM:
                return new Reading(toHumanSize(-random.nextFloat() * 10), freezeLevel);
            case HIGH:
                return new Reading(toHumanSize(-random.nextFloat() * 30 - 3), freezeLevel);
        }
        throw new UnusporrtedTypeException();
    }

    private float toHumanSize(float size) {
        return Float.valueOf(formatter.format(size).replace(',', '.'));
    }
}