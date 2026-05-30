package pbo.f01.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_area")
public class Parkir {
    @Id
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "allowed_type", length = 50)
    private String allowed_type;

    @Column(name = "capacity")
    private int capacity;

    // Relasi One-to-Many mendeteksi daftar kendaraan yang terparkir di area ini
    @OneToMany(mappedBy = "parkingArea", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Vehicle> vehicles = new ArrayList<>();

    public Parkir() {}

    public Parkir(String name, String allowed_type, int capacity) {
        this.name = name;
        this.allowed_type = allowed_type;
        this.capacity = capacity;
    }

    public String getName() {
        return name; 
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllowed_type() {
        return allowed_type; 
    }

    public void setAllowed_type(String allowed_type) {
        this.allowed_type = allowed_type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public String toString() {
        return name + " " + allowed_type + " " + capacity + "|" + (vehicles != null ? vehicles.size() : 0);
    }
}