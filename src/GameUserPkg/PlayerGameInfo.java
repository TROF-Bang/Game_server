/*
 * Author : Gompang
 * Desc : 네트워크 게임에서 사용되는(채팅도 포함) 방 개념 클래스
 * Blog : http://gompangs.tistory.com/
 */
package GameUserPkg;

// 유저의 상태 및 현재 위치하고 있는 장소를 지정하기 위한 Enum Class
public class PlayerGameInfo {

	enum Location {
		MAP_1, MAP_2, MAP_3, MAP_4, MAP_5
	};

	enum Status {
		IDLE, BATTLE, DEAD
	};
}