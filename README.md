# MyTube
Parsing Youtube Data & Save at Android Device Using SQLite

## 설계 배경
동영상(.mp4, .avi) 재생 환경을 제공해주는 플랫폼을 Android Application으로 구현

## 설계 범위
- SQLite를 활용해 내장 메모리에 동영상 데이터베이스를 구축
- 릴레이션 구성은 **①썸네일 URL**, **②업로드 날짜**, **③제목**, **④동영상 ID**, **⑤좋아요 클릭 시간**으로 구성

## 설계 내용
![ER](https://user-images.githubusercontent.com/20378368/105571677-6327e000-5d95-11eb-9ec6-21f2d4646e3d.png)
- **VIDEO_DATA**: "YouTube Data API v3"를 활용해 동영상 정보를 파싱하고, 동영상 재생에 필요한 정보인 VIDEO_ID, TITLE, URL, PUBLISHED_AT를 받아옴
- **VIDEO_STORE**: VIDEO_DATA의 애트리뷰트 전체를 상속받고 특히 VIDEO_ID를 외래키로 받아와, 테이블의 새로운 기본키로 지정, 추가적으로 PLAYED와 LIKE_AT 애트리뷰트를 생성해 정렬 질의에서 활용
- **SEARCH**: SEARCH라는 파라미터를 사용자로부터 받아 검색 질의를 이용해 부합하는 영상을 화면에 출력
- **GROUP_LIST**: 사용자가 그룹 이름을 생성할 수 있게 하고, SEARCH 테이블과 연동하여 질의를 통해 그룹 내역을 화면에 출력

## 데이터베이스 스키마
| Content | Description |
| --- | --- |
| VIDEO_ID | 각 동영상마다 고유한 Video ID |
| TITLE | 동영상의 제목, 30만큼의 길이를 가짐 |
| URL | 동영상의 URL, 30만큼의 길이를 가짐 |
| PUBLISHED_AT | 동영상이 업로드된 날짜, Date 형식 |
| LIKE_AT | 좋아요를 클릭한 날짜, Date 형식 |
| GROUP_NAME | 사용자가 정의한 그룹 명, 10만큼의 길이를 가짐 |
| SEARCH | 검색 기능에서 임시로 저장되는 키워드 |
| PLAYED | 동영상이 재생된 횟수를 누적한 값 |

## UI 설계
- 검색 화면  
![전체](https://user-images.githubusercontent.com/20378368/105571797-4dff8100-5d96-11eb-8f0d-63d31bd256ba.PNG)
- 동영상 재생 화면  
![재생](https://user-images.githubusercontent.com/20378368/105571802-64a5d800-5d96-11eb-8361-a2924aee90d9.PNG)
- 보관함 화면  
![보관함](https://user-images.githubusercontent.com/20378368/105571804-67083200-5d96-11eb-96f4-f51b64e72537.PNG)
- 옵션 화면  
![옵션](https://user-images.githubusercontent.com/20378368/105571805-68395f00-5d96-11eb-8945-5e32daf9a2fc.PNG)
