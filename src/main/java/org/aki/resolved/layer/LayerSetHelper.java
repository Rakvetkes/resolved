package org.aki.resolved.layer;

public class LayerSetHelper {

    private static final FluidLayerSet NULL_LAYER_SET = new FluidLayerSet();

    public static FluidLayerSet getNull() {
        return NULL_LAYER_SET;
    }

    public static FluidLayerSet getFromConstituent(Object constituent) {
        int consId = ConstituentRegistry.REGISTRY.get(constituent);
        float unitVolume = ConstituentRegistry.REGISTRY.getAttributes(consId).volume();
        return new FluidLayerSet(new FluidLayer(new Constituent(consId, 1000.0f / unitVolume)));
    }

    public static int getColor(FluidLayerSet layerSet) {
        return 0xDDEEFF; // todo
    }

}
