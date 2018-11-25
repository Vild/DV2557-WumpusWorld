package wumpusworld.neuralnetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import wumpusworld.MapGenerator;
import wumpusworld.MapReader;
import wumpusworld.MyAgent;
import wumpusworld.World;
import wumpusworld.WorldMap;
import wumpusworld.WumpusWorld;

/**
 *
 * @author Dan Printzell
 */
public class Generation {

    int generationNumber = 0;
    final int CPU_CORES = 12;
    final int NETWORK_COUNT = 1024*2;
    private ArrayList<Network> networks;
    final ThreadPoolExecutor executor;

    /**
     * Generation a new random generation.
     */
    public Generation() {
        this.networks = new ArrayList<>(NETWORK_COUNT);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(CPU_CORES);

        for (int i = 0; i < NETWORK_COUNT; i++)
            networks.add(new Network(null));
    }

    /**
     * Loads a previous generated generation.
     *
     * @param folderPath The folder where the generation lives.
     */
    public Generation(String folderPath) {
        this.networks = new ArrayList<>(NETWORK_COUNT);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(CPU_CORES);
        // TODO: load

        for (int i = 0; i < NETWORK_COUNT; i++)
            networks.add(new Network(null, String.format("%s/network_%02d.dat", folderPath, i)));
    }

    /**
     * Creates a new generation with the networks 'networks'.
     *
     * @param networks The new networks.
     */
    public Generation(ArrayList<Network> networks) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(CPU_CORES);
        this.networks = networks;
    }

    public void run() {
        long seed = System.currentTimeMillis();
        for (int i = 0; i < NETWORK_COUNT; i++)
            executor.execute(new ChildLearningTask(networks.get(i), seed));
        executor.shutdown();
        System.out.println();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(WumpusWorld.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.print("\r#" + generationNumber + ": " + (executor.getCompletedTaskCount() * 100f) / NETWORK_COUNT + "% done!");
        }
        System.out.println();
        System.out.println("Finished all threads");

        Collections.sort(networks, (Network o1, Network o2) -> {
            float a = o1.getFinalScore();
            float b = o2.getFinalScore();
            if (a == b)
                return 0;

            return a > b ? -1 : 1;
        });

        System.out.println("Top 10 networks for generation " + generationNumber + ":");
        for (int i = 0; i < 10; i++)
            System.out.println("\t" + i + ": " + networks.get(i).getFinalScore());

    }

    public void save(String prefix) {
        for (int i = 0; i < networks.size(); i++)
            networks.get(i).save(prefix, String.format("network_%02d.dat", i));
    }

    public Generation mutateNewGeneration() {
        Network best = networks.get(0);
        Network secondBest = networks.get(1);

        ArrayList<Network> newNetworks = new ArrayList<>(NETWORK_COUNT);
        for (int i = 0; i < NETWORK_COUNT / 4; i++)
            newNetworks.add(networks.get(i).clone());
        for (int i = 0; i < (NETWORK_COUNT - (NETWORK_COUNT / 4)); i++) {
            Network n = best.combine(secondBest);
            n.addMutations();
            newNetworks.add(n);
        }

        Generation g = new Generation(newNetworks);
        g.generationNumber = generationNumber + 1;
        return g;
    }

    class ChildLearningTask implements Runnable {

        private final int MAP_COUNT = 8;
        private final Network network;
        private final long seed;

        public ChildLearningTask(Network network, long seed) {
            this.network = network;
            this.seed = seed;
        }

        @Override
        public void run() {
            World world = null;
            MyAgent agent = new MyAgent(world, network);
            MapReader mr = new MapReader();
            Vector<WorldMap> maps = mr.readMaps();
            for (int i = 0; i < /*maps.size() +*/ MAP_COUNT; i++) {
                /*if (i < maps.size())
                    world = maps.get(i).generateWorld();
                else*/
                world = MapGenerator.getRandomMap((int) (seed + i)).generateWorld();

                agent.setWorld(world);
                int actions = 0;
                while (!world.gameOver() && actions < 100 && !network.scoreHandler.isDone()) {
                    agent.doAction();
                    actions++;
                }
                if (actions >= 100)
                    network.scoreHandler.noMoreMoves();
                network.scoreHandler.submitWorldScore(world.getScore());
            }
        }
    }

}
