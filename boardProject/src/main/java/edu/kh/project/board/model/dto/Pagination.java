package edu.kh.project.board.model.dto;

/* Pagination 뜻 : 목록을 일정 페이지로 분할해서 
 * 원하는 페이지를 볼 수 있게 하는 것 == 페이징 처리
 * 
 * Pagination 객체를 만드는 이유 : 페이징 처리에 필요한 값을 모아두고, 계산을 하는 객체의 역할로 만들기 위함
 */
public class Pagination {
	private int currentPage; // 현재 페이지 번호
	private int listCount; // 전체 게시글 수, selectBoardList에서 listCount로 가져옴

	private int limit = 10; // 한 페이지 목록에 보여지는 게시글 수
	private int pageSize = 10; // 보여질 페이지 번호 개수, 즉 1~10페이지 나타내는 전체 페이지 크기
	// 만약 pageSize가 5라면 1~5, 6~10 이렇게 보임

	// 아래 5가지는 값을 직접 바꿔줘야하는 필드
	// 가장 첫페이지는 당연히 1페이지 -> 그래서 minPage는 따로없음
	// minPage는 가장 첫번째 페이지를 의미
	private int maxPage; // 마지막 페이지 번호
	private int startPage; // 보여지는 맨 앞 페이지 번호
	private int endPage; // 보여지는 맨 뒤 페이지 번호
	// startPage, endPage : < or > 버튼 클릭 시, 시작과 끝을 의미
	// 1~10이면 startPage = 1, endPage = 10

	private int prevPage; // 이전 페이지 모음의 마지막 번호
	private int nextPage; // 다음 페이지 모음의 시작 번호
	// < or >는 다음페이지의 첫번째 페이지로 가야함
	// 만약 1~10페이지에서 >를 누르면 -> 11~20 페이지 중 11페이지 값이 보여야 함
	// 만약 현재 21~30이면
	// < 눌렀을 때 -> 20번 페이지
	// > 눌렀을 때 -> 31번 페이지

	// 기본 생성자 X (필요 없음) -> 페이지 네이션 계산을 하는 용도가 안되기 때문

	// 2개짜리 생성자(currentPage, listCount)
	public Pagination(int currentPage, int listCount) {
		this.currentPage = currentPage;
		this.listCount = listCount;

		calculate();
	}

	// 4개짜리 생성자(currentPage, listCount, limit, pageSize)
	public Pagination(int currentPage, int listCount, int limit, int pageSize) {
		this.currentPage = currentPage;
		this.listCount = listCount;
		this.limit = limit;
		this.pageSize = pageSize;

		calculate();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getListCount() {
		return listCount;
	}

	public int getLimit() {
		return limit;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		calculate();
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
		calculate();

	}

	public void setLimit(int limit) {
		this.limit = limit;
		calculate();

	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		calculate();

	}

	@Override
	public String toString() {
		return "Pagination [currentPage=" + currentPage + ", listCount=" + listCount + ", limit=" + limit
				+ ", pageSize=" + pageSize + ", maxPage=" + maxPage + ", startPage=" + startPage + ", endPage="
				+ endPage + ", prevPage=" + prevPage + ", nextPage=" + nextPage + "]";
	}

	/**
	 * 페이징 처리에 필요한 값을 계산해서 필드에 대입하는 메서드 (startPage, endPage, maxPage, prevPage,
	 * nextPage)
	 */
	private void calculate() {
		// 필드 중 limit을 10이라고 가정
		// 만약 게시글이 240개면 maxPage는 24
		// 만약 게시글이 241개면 maxPage는 25

		// 1. maxPage : 최대 페이지 == 즉, 마지막 페이지를 의미하는 것 == 총 페이지 수를 나타냄

		// 한 페이지에 게시글이 10개(limit)씩 보여질 경우
		// 게시글 수 = 95개라면 -> 10페이지가 나와야 함
		// 게시글 수 = 100개라면 -> 10페이지가 나와야 함
		// 게시글 수 = 101개라면 -> 11페이지
		maxPage = (int) Math.ceil((double) listCount / limit);
		// 만약 95 / 10 -> 9
		// (double) 95 / 10 -> 9.5
		// ceil이면 10.0 -> int로 바꾸면 10
		// maxPage는 10

		// 2. startPage : 페이지 번호 목록의 시작 번호
		// 페이지 번호 목록이 10개(pageSize)씩 보여지는 경우
		// 21~30페이지가 있다면 21, 시작 페이지를 구하겠다는 의미

		// 현재 페이지가 1 ~ 10 라면 : 1페이지
		// 현재 페이지가 11 ~ 20 라면 : 11페이지

		startPage = (currentPage - 1) / pageSize * pageSize + 1;
		// 만약 현재 5페이지에 있다면
		// (5-1) / (pageSize)10 * 10 + 1 => 1
		// 시작 페이지 1

		// 만약 10페이지라면
		// 0 * 0 + 1 => 1

		// 만약 11페이지라면
		// 1 * 10 + 1 => 11

		// 3. endPage : 페이지 번호 목록의 마지막 번호
		endPage = pageSize - 1 + startPage;
		// 만약 15페이지에 있다면
		// 9 + 11 = 20

		// 페이지 끝 번호가 최대 페이지 수를 초과한 경우

		if (endPage > maxPage) {
			// 만약 endPage가 70이고, maxPage가 68이라면
			// endPage는 유효한 게시판 값들이 존재하는 maxPage로 대체해줘야 한다.
			endPage = maxPage;
		}

		// 4. prevPage
		// prevPage : "<" 클릭 시 이동할 페이지 번호
		// (이전 레벨 번호 목록 중 마지막 번호)

		if (currentPage <= pageSize) {
			// 더 이상 이전으로 갈 페이지가 없을 경우
			prevPage = 1;
		} else {
			prevPage = startPage - 1;
		}

		// 5. nextPage
		// nextPage : ">" 클릭 시 이동할 페이지 번호
		// (다음 레벨 번호 목록 중 시작 번호)
		// 레벨은 10페이지씩 넘어가는 것을 의미

		if (endPage == maxPage) {
			// 더 이상 다음으로 넘어갈 페이지가 없을 경우
			nextPage = maxPage;
		} else {
			nextPage = endPage + 1;
		}
	}

}
