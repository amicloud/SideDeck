package com.outplaysoftworks.sidedeck;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Billy on 6/8/2016.
 */
public class RandomAnimationBuilder {

    private Integer frameCount;
    private Integer duration;
    private ArrayList<Drawable> drawables;
    private Integer frameDuration;
    private Random random = new Random(System.nanoTime());

    /**
     * Used to create an animation from a series of drawables with the frames in
     * a completely random order with uniform duration. Random generator uses System.nanoTime by
     * default
     * @param drawables Arraylist of drawables to use for individual frames
     * @param duration Total animation duration
     * @param frameCount Total frames for animation
     */
    public RandomAnimationBuilder(ArrayList<Drawable> drawables, Integer duration, Integer frameCount){
        this.drawables = drawables;
        this.duration = duration;
        this.frameCount = frameCount;
        this.frameDuration = duration/frameCount;
    }

    /**
     * Creates a new random number generator for the Builder to use in .make()
     * @param seed Wise men plant seeds for trees that they know they will not sit in the shade of
     */
    public void createNewRandomNumberGenerator(long seed){
        this.random = new Random(seed);
    }

    /**
     * Creates a new random number generator for the Builder to use in .make() with System.nanoTime
     */
    public void createNewRandomNumberGenerator(){
        this.random = new Random(System.nanoTime());
    }

    /**
     * Gets the computed frame duration for this builder
     * @return Frame duration in milliseconds
     */
    public Integer getFrameDuration(){
        return frameDuration;
    }

    /**
     * Contructs an Animation Drawable using resources in the RandomAnimationBuilder object
     * @param allowIdenticalConsecutiveFrames
     * @return Animation Drawable ready to use
     */
    public AnimationDrawable makeAnimation(boolean allowIdenticalConsecutiveFrames){
        AnimationDrawable animationDrawable = new AnimationDrawable();
        int max = drawables.size();
        int lastRandomNumber;
        int rand = 0;
        int i = 0;
        while(i < frameCount) {
            lastRandomNumber = rand;
            rand = random.nextInt(max);
            if(allowIdenticalConsecutiveFrames || lastRandomNumber != rand) {
                animationDrawable.addFrame(drawables.get(rand), frameDuration);
                i++;
            }
        }
        return animationDrawable;
    }
}
