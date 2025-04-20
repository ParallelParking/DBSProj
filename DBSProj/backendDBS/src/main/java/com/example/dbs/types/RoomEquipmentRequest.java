package com.example.dbs.types;

// DTO for adding equipment to a room
public class RoomEquipmentRequest {
    private String block;
    private String room; // Room identifier (matches Room entity's 'room' field)
    private String type; // Equipment type identifier (matches Equipment entity's 'type' field)

    public RoomEquipmentRequest() {}

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
