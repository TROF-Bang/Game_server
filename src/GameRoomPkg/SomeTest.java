package GameRoomPkg;

public class SomeTest {
    public static void main(String[] args) {
        System.out.println(RoomManager.createRoom());
        System.out.println(RoomManager.createRoom());

        Thread thread = new Thread(() -> System.out.println(RoomManager.createRoom()));
        Thread thread2 = new Thread(() -> System.out.println(RoomManager.createRoom()));
        Thread thread3 = new Thread(() -> System.out.println(RoomManager.createRoom()));
        thread.start();
        thread2.start();
        thread3.start();
    }
}