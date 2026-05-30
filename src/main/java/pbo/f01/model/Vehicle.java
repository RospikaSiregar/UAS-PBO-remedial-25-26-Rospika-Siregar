package pbo.f01.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @Column(name = "plate_number", length = 20)
    private String plate_number;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "owner", length = 150)
    private String owner;

    // Relasi Many-to-One memetakan penempatan kendaraan ke area parkir tertentu
    @ManyToOne
    @JoinColumn(name = "parking_area_name", referencedColumnName = "name")
    private Parkir parkingArea;

    public Vehicle() {}

    public Vehicle(String plate_number, String type, String owner) {
        this.plate_number = plate_number;
        this.type = type;
        this.owner = owner;
    }

    public String getPlate_number() {
        return plate_number; 
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getType() {
        return type; 
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner; 
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Parkir getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(Parkir parkingArea) {
        this.parkingArea = parkingArea;
    }

    @Override
    public String toString() {
        return plate_number + " " + owner + " " + type;
    }
}