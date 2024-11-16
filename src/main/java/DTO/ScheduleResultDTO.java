
package DTO;

import java.util.List;

public class ScheduleResultDTO {
    private double fitnessScore;
    private List<Integer> bestSolution;
    private int iterations;

    // Getters e Setters
    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    public List<Integer> getBestSolution() {
        return bestSolution;
    }

    public void setBestSolution(List<Integer> bestSolution) {
        this.bestSolution = bestSolution;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
