package repository;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.Board;
import domain.PagingVO;
import orm.DatabaseBuilder;

public class BoardDAOImpl implements BoardDAO {
	
	private static final Logger log = LoggerFactory.getLogger(BoardDAOImpl.class);
	private SqlSession sql;
	
	public BoardDAOImpl() {
		new DatabaseBuilder();
		sql = DatabaseBuilder.getFactory().openSession();
	}

	@Override
	public int insert(Board board) {
		// TODO Auto-generated method stub
		int isOk = sql.insert("boardMapper.add", board);
		if(isOk > 0) sql.commit();
		return isOk;
	}

	@Override
	public List<Board> getList(PagingVO pagingVO) {
		// TODO Auto-generated method stub
		return sql.selectList("boardMapper.list", pagingVO);
	}

	@Override
	public int getTotal(PagingVO pagingVO) {
		// TODO Auto-generated method stub
		return sql.selectOne("boardMapper.cnt", pagingVO);
	}

	@Override
	public Board getDetail(int bno) {
		// TODO Auto-generated method stub
		return sql.selectOne("boardMapper.detail", bno);
	}

	@Override
	public Board getModify(int bno) {
		// TODO Auto-generated method stub
		return sql.selectOne("boardMapper.modify", bno);
	}

	@Override
	public int update(Board board) {
		// TODO Auto-generated method stub
		int isOk = sql.update("boardMapper.update", board);
		if(isOk > 0) sql.commit();
		return isOk;
	}

	@Override
	public void delete(int bno) {
		// TODO Auto-generated method stub
		int isOk = sql.delete("boardMapper.del",bno);
		if(isOk > 0) sql.commit();
		
	}

}
