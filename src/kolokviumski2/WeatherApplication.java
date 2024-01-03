package kolokviumski2;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if(parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);
                if(operation==1) {
                    weatherDispatcher.remove(forecastDisplay);
                }
                if(operation==2) {
                    weatherDispatcher.remove(currentConditions);
                }
                if(operation==3) {
                    weatherDispatcher.register(forecastDisplay);
                }
                if(operation==4) {
                    weatherDispatcher.register(currentConditions);
                }

            }
        }
    }
}
class WeatherDispatcher implements Subject{
    private Set<Updatable> updatables;
    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherDispatcher() {
        updatables = new HashSet<>();
    }
    @Override
    public void register(Updatable o) {
        updatables.add(o);
    }
    @Override
    public void remove(Updatable o) {
        updatables.remove(o);
    }
    @Override
    public void notifyUpdatable() {
        updatables.forEach(u->u.update(temperature,humidity,pressure));
    }
    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        notifyUpdatable();
    }
}
interface Updatable{
    void update(float temp, float humidity, float pressure);
}
interface Displayable {
    void display();
}
interface Subject{
    void register(Updatable o);
    void remove(Updatable o);
    void notifyUpdatable();
}
class CurrentConditionsDisplay implements Updatable, Displayable{
    private float temperature;
    private float humidity;
    private Subject weatherStation;

    public CurrentConditionsDisplay(Subject weatherStation) {
        this.weatherStation = weatherStation;
        this.weatherStation.register(this);
    }

    @Override
    public void update(float temp, float humidity, float pressure) {
        this.temperature = temp;
        this.humidity = humidity;
        display();
    }

    @Override
    public void display() {
        System.out.println("Temperature: " + temperature + "F");
        System.out.println("Humidity: " + humidity + "%");
    }
}

class ForecastDisplay implements Updatable, Displayable{
    private float currentPressure = 0.0f;
    private float lastPressure;
    private WeatherDispatcher weatherDispatcher;

    public ForecastDisplay(WeatherDispatcher weatherDispatcher) {
        this.weatherDispatcher = weatherDispatcher;
        this.weatherDispatcher.register(this);
    }

    @Override
    public void update(float temp, float humidity, float pressure) {
        lastPressure = currentPressure;
        currentPressure = pressure;
        display();
    }

    @Override
    public void display() {
        System.out.print("Forecast: ");
        if (currentPressure > lastPressure) {
            System.out.println("Improving");
        } else if (currentPressure == lastPressure) {
            System.out.println("Same");
        } else {
            System.out.println("Cooler");
        }
        System.out.println();
    }
}