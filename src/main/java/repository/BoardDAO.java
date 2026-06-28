package repository;

import java.util.List;

import domain.Board;
import domain.PagingVO;

public interface BoardDAO {

	int insert(Board board);

	List<Board> getList(PagingVO pagingVO);

	int getTotal(PagingVO pagingVO);

	Board getDetail(int bno);

	Board getModify(int bno);

	int update(Board board);

	void delete(int bno);

}
