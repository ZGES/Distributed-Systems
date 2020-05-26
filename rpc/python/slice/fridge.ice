#ifndef FRIDGE_ICE
#define FRIDGE_ICE

module Fridge {

    enum Level {
        LOW,
        MEDIUM,
        HIGH
    };

    struct Reading {
        float temperature;
        Level freezeLevel;
    };

    exception UnusporrtedTypeException {
        string message = "This type is unsupported";
    };

    interface FridgeControl {
        Reading checkTemp(Level freezeLevel) throws UnusporrtedTypeException;
    };

};

#endif