package com.bingo;

import java.lang.Thread;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        assert(args.length > 0);
        int tCount = Integer.parseInt(args[0]);
        TrafficLightController.init();
        for (int i = 0; i < tCount; i++) {
            TrafficLightController controller = new TrafficLightController(i);
            controller.start();
        }
    }
}

class TrafficLight implements Cloneable{

    enum Color {RED, YELLOW, GREEN};
    private static final TrafficLight _instance = new TrafficLight(0);

    private int id;
    private Color color;
    private ReentrantLock lock;

    private TrafficLight(int id) {
        this.id = id;
        this.color = Color.GREEN;
        this.lock = new ReentrantLock(true);
    }

    public static TrafficLight getNewTrafficLight(int id) {
        TrafficLight tl = null;
        try {
            tl = (TrafficLight)_instance.clone();
        } catch (CloneNotSupportedException e) {
            //We're pretty sure it supports clone!
        }
        tl.setId(id);
        //IMPORTANT: replace with new lock
        tl.setLock(new ReentrantLock(true));
        return tl;
    }

    private static String nameOfColor(Color color) {
        if (color == Color.GREEN)
            return "green";
        if (color == Color.RED)
            return "red";
        //Color.YELLOW
        return "yellow";
    }

    private static Color colorById(int id) {
        if (id == 1)
            return Color.RED;
        if (id == 2)
            return Color.GREEN;
        return Color.YELLOW;
    }

    private static Color next(Color color) {
        if (color == Color.GREEN)
            return Color.YELLOW;
        if (color == Color.YELLOW)
            return Color.RED;
        //Color.GREEN
        return Color.GREEN;
    }

    public void trigger() {
        lock.lock();
        System.out.println(String.format("TrafficLight %d color %s before trigger!", id,
                TrafficLight.nameOfColor(color)));
        try{
            color = TrafficLight.next(color);
            System.out.println(String.format("TrafficLight %d color %s after trigger!", id,
                    TrafficLight.nameOfColor(color)));
        }finally{
            lock.unlock();
        }
    }

    public final void setId(int id) { this.id = id;}

    public void setLock(ReentrantLock lock) { this.lock = lock;}

    public final Color color() {
        return color;
    }

    public final String colorName() {
        return TrafficLight.nameOfColor(color);
    }
}

class TrafficLightController extends Thread {
    public static final int NUMBER_OF_LIGHT = 10;
    //Traffic light map to remember each light's color
    private static ConcurrentHashMap<Integer, TrafficLight> lights;
    private int id;


    public TrafficLightController(int id) {
        this.id = id;
    }



    public static final void init() {
        lights = new ConcurrentHashMap<Integer, TrafficLight>(NUMBER_OF_LIGHT);
        for (int i = 0; i < NUMBER_OF_LIGHT; i++) {
            lights.put(new Integer(i), TrafficLight.getNewTrafficLight(i));
        }
    }


    @Override
    public void run() {
        while (true) {
            randomTurnLight();
        }
    }

    private void randomTurnLight() {
        //keep running and change random light color
        Random rand = new Random();
        int lightNumb = rand.nextInt(TrafficLightController.NUMBER_OF_LIGHT);
        Integer lightKey = new Integer(lightNumb);
        TrafficLight light = lights.get(lightKey);
        light.trigger();
        //Sleep for a while
        /*try {
            Thread.sleep(500);
        }catch(InterruptedException e) {
            //keep running even I'm interrupted
        }*/
    }
}