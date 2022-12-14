package org.movie.DAO;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.movie.DTO.ShowingTimeDTO;
import org.movie.DTO.CinemaDTO;
import org.movie.DTO.MemberDTO;
import org.movie.DTO.MovieDTO;
import org.movie.DTO.MypageTicketDTO;
import org.movie.DTO.ReviewDTO;
import org.movie.DTO.SeatInforDTO;
import org.movie.DTO.TicketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TicketDAO {

	@Autowired
	private SqlSession session;
	private String ticketMapper = "org.movie.mappers.ticket";
	private static TicketDAO ticket;
	
	public TicketDAO() {}
	
	public static TicketDAO getInstance() {
		
		if(ticket == null){
            synchronized (TicketDAO.class) {
                if(ticket == null)
                	ticket = new TicketDAO();
            }
        }
		return ticket;
	}
	
	public List<MemberDTO> ticketMemberSelect(String movie_id) {
		List<MemberDTO> ticketMemberList = session.selectList(ticketMapper + ".ticketMemberSelect", movie_id);
		return ticketMemberList;
	}

	public List<MypageTicketDTO> mypageTicketList(String id) {
		List<MypageTicketDTO> mypageTicketList = session.selectList(ticketMapper + ".mypageTicketList", id);
		return mypageTicketList;
	}
	
	public List<MovieDTO> ticketMovieList() {
		List<MovieDTO> ticketMovieList = session.selectList(ticketMapper + ".ticketMovieList");
		return ticketMovieList;
	}

	public List<CinemaDTO> ticketCinemaList() {
		List<CinemaDTO> ticketCinemaList = session.selectList(ticketMapper + ".ticketCinemaList");
		return ticketCinemaList;
	}

	public List<ShowingTimeDTO> ticketShowingTimeList(HashMap<String, Object> map) {
		List<ShowingTimeDTO> ticketShowingTimeList = session.selectList(ticketMapper + ".ticketShowingTimeList", map);
		return ticketShowingTimeList;
	}
	
	public void ticketInsert(HashMap<String, Object> map) {
		session.insert(ticketMapper + ".ticketInsert", map);
	}

	public SeatInforDTO ticketSeatInfor(int cinema_seat_id) {
		SeatInforDTO ticketSeatInfor = session.selectOne(ticketMapper + ".ticketSeatInfor" , cinema_seat_id);
		return ticketSeatInfor;
	}

	public List<String> reservedSeats(int cinema_showing_id) {
		List<String> reservedSeats = session.selectList(ticketMapper + ".reservedSeats", cinema_showing_id);
		return reservedSeats;
	}

	public void ticketCancel(HashMap<String, String> map) {
		session.update(ticketMapper + ".ticketCancel", map);
	}

}
