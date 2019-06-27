package gr.cite.gaap.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;


public class SeedGWCRequest implements Serializable {

    String name;
    int zoomStart;
    int zoomStop;
    int threadCount;
    String gridSetId;
    String type;
    Bounds bounds;
//    String parameters;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getZoomStart() {
        return zoomStart;
    }

    public void setZoomStart(int zoomStart) {
        this.zoomStart = zoomStart;
    }

    public int getZoomStop() {
        return zoomStop;
    }

    public void setZoomStop(int zoomStop) {
        this.zoomStop = zoomStop;
    }

    public String getGridSetId() {
        return gridSetId;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setGridSetId(String gridSetId) {
        this.gridSetId = gridSetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public void setBounds(double minX, double minY, double maxX, double maxY) {
        double [] coords = {minX, minY, maxX, maxY};
        this.bounds = new Bounds(coords);
    }

    private class Bounds {
        private Coords coords;

        public Bounds(double[] coords) {
            this.coords = new Coords(coords);
        }

        public Coords getCoords() {
            return coords;
        }

        public void setCoords(Coords coords) {
            this.coords = coords;
        }

        private class Coords {
            @JsonProperty("double")
            double[] doubles;

            public double[] getDoubles() {
                return doubles;
            }

            public void setDoubles(double[] doubles) {
                this.doubles = doubles;
            }

            public Coords(double[] doubles) {
                this.doubles = doubles;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeedGWCRequest)) return false;
        SeedGWCRequest that = (SeedGWCRequest) o;
        return zoomStart == that.zoomStart &&
                zoomStop == that.zoomStop &&
                Objects.equals(name, that.name) &&
                Objects.equals(gridSetId, that.gridSetId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(bounds, that.bounds);// &&
//                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, zoomStart, zoomStop, gridSetId, type, bounds);
    }
}
