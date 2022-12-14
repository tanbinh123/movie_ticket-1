package org.movie.ticket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.movie.DAO.CinemaDAO;
import org.movie.DAO.GenreDAO;
import org.movie.DAO.MovieDAO;
import org.movie.DAO.ReviewDAO;
import org.movie.DAO.TicketDAO;
import org.movie.DTO.ActorDTO;
import org.movie.DTO.CinemaDTO;
import org.movie.DTO.CinemaImageDTO;
import org.movie.DTO.DirectorDTO;
import org.movie.DTO.GenreDTO;
import org.movie.DTO.MemberDTO;
import org.movie.DTO.MovieDTO;
import org.movie.DTO.ReviewDTO;
import org.movie.DTO.SeatInforDTO;
import org.movie.DTO.ShowingTimeDTO;
import org.movie.DTO.TrailerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
public class IndexController {
	
	@Autowired
	private CinemaDAO cinemadao = CinemaDAO.getInstance();
	@Autowired
	private MovieDAO moviedao = MovieDAO.getInstance();
	@Autowired
	private GenreDAO genredao = GenreDAO.getInstance();
	@Autowired
	private ReviewDAO reviewdao = ReviewDAO.getInstance();
	@Autowired
	private TicketDAO ticketdao = TicketDAO.getInstance();
	
	// 헤더 영화관 메뉴 데이터 불러오기
	public void cinemaMenu(Model model) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("target", "address");
		map.put("search", "");
		
		List<CinemaDTO> cinemaList = cinemadao.cinemaList(map);
		model.addAttribute("cinemaList", cinemaList);
	}
	
	// 메인페이지 
	@RequestMapping({"/index", "/"})
	public String index(Model model) {
		cinemaMenu(model);

		List<MovieDTO> movieShowingList = moviedao.movieShowingListTop10();
		model.addAttribute("movieShowingList", movieShowingList);

		List<MovieDTO> movieNotShowingList = moviedao.movieNotShowingListTop10();
		model.addAttribute("movieNotShowingList", movieNotShowingList);

		return "index";
	}
	
	// 헤더 영화관 찾기 메뉴
	@RequestMapping(value="/cinema_search_menu", method=RequestMethod.GET)
	public @ResponseBody List<CinemaDTO> cinema_search_menu(String target, String search) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("target", target);
		map.put("search", search);
		
		List<CinemaDTO> cinemaList = cinemadao.cinemaList(map);
		return cinemaList;
	}
	
	// 영화 검색 - 장르 검색
	@RequestMapping(value="/movie_genre_search", method=RequestMethod.GET, produces = "application/text; charset=utf8")
	public @ResponseBody String movie_genre_search(String search) {
		List<MovieDTO> movieGenreSearchShowingList = null;
		List<MovieDTO> movieGenreSearchNotShowingList =null;
		
		if (search.equals("전체")) {
			movieGenreSearchShowingList = moviedao.movieShowingList();
			movieGenreSearchNotShowingList = moviedao.movieNotShowingList();
		} else {
			int searchNo = genredao.genreSelect(search);
			movieGenreSearchShowingList = moviedao.movieGenreSearchShowingList(searchNo);
			movieGenreSearchNotShowingList = moviedao.movieGenreSearchNotShowingList(searchNo);
		}
		
		JsonObject genreSearchResult = new JsonObject();
		Gson gson = new Gson();
		genreSearchResult.addProperty("now", gson.toJson(movieGenreSearchShowingList));
		genreSearchResult.addProperty("plan", gson.toJson(movieGenreSearchNotShowingList));
		String result = gson.toJson(genreSearchResult);
		return result;
	}
	
	// 영화 검색 - 제목 검색
	@RequestMapping(value="/movie_title_search", method=RequestMethod.GET, produces = "application/text; charset=utf8")
	public @ResponseBody String movie_title_search(String search) {
		List<MovieDTO> movieTitleSearchShowingList = moviedao.movieTitleSearchShowingList(search);
		List<MovieDTO> movieTitleSearchNotShowingList = moviedao.movieTitleSearchNotShowingList(search);
		
		JsonObject titleSearchResult = new JsonObject();
		Gson gson = new Gson();
		titleSearchResult.addProperty("now", gson.toJson(movieTitleSearchShowingList));
		titleSearchResult.addProperty("plan", gson.toJson(movieTitleSearchNotShowingList));
		String result = gson.toJson(titleSearchResult);
		return result;
	}
	
	// 리뷰 좋아요 추가 
	@RequestMapping(value="/like_count_update", method=RequestMethod.GET, produces = "application/text; charset=utf8")
	public @ResponseBody String like_count_update(String review_id, int count) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("count",  count);
		map.put("review_id", review_id);
		reviewdao.likeCountUpdate(map);
		return "수정되었습니다";
	}
	
	// 영화관 상세 페이지 
	@RequestMapping(value="/cinema_detail", method=RequestMethod.GET)
	public String cinema_detail(@RequestParam String no, Model model) { 
		cinemaMenu(model);
		
		CinemaDTO cinema = cinemadao.selectOne(no);
		model.addAttribute("cinema", cinema);
		
		List<MovieDTO> cinemaShowingMovieList = cinemadao.cinemaShowingMovieList(no);
		model.addAttribute("cinemaShowingMovieList", cinemaShowingMovieList);

		List<MovieDTO> cinemaNotShowingMovieList = cinemadao.cinemaNotShowingMovieList(no);
		model.addAttribute("cinemaNotShowingMovieList", cinemaNotShowingMovieList);
		
		List <CinemaImageDTO> cinemaImages = cinemadao.cinemaImagesList(no);
		model.addAttribute("cinemaImages", cinemaImages);

		return "cinema_detail";
	}
	
	// 영화 목록 페이지 
	@RequestMapping(value="/movie", method=RequestMethod.GET)
	public String movie(Model model) { 
		cinemaMenu(model);

		List<GenreDTO> genreList = genredao.genreList();
		model.addAttribute("genreList", genreList);

		List<MovieDTO> movieShowingList = moviedao.movieShowingList();
		model.addAttribute("movieShowingList", movieShowingList);

		List<MovieDTO> movieNotShowingList = moviedao.movieNotShowingList();
		model.addAttribute("movieNotShowingList", movieNotShowingList);

		return "movie";
	}
	
	// 영화 상세 페이지 
	@RequestMapping(value="/movie_detail", method=RequestMethod.GET)
	public String movie_detail(@RequestParam String no, Model model) throws ParseException { 
		cinemaMenu(model);

		// 영화 정보
		MovieDTO movieInfor = moviedao.movieSelectOne(no);
		model.addAttribute("movieInfor", movieInfor);
		
		// 영화 장르
		List<String> movieGenreList = genredao.movieGenreList(no);
		model.addAttribute("movieGenreList", movieGenreList);
		
		// 영화 감독
		List<DirectorDTO> movieDirectorList = moviedao.movieDirectorList(no);
		model.addAttribute("movieDirectorList", movieDirectorList);
		
		// 영화 배우
		List<ActorDTO> movieActorList = moviedao.movieActorList(no);
		model.addAttribute("movieActorList", movieActorList);
		
		// 영화 예고편
		List<TrailerDTO> movieTrailerList = moviedao.movieTrailerList(no);
		model.addAttribute("movieTrailerList", movieTrailerList);
		
		// 영화 스틸컷이미지
		List<String> movieImageList = moviedao.movieImageList(no);
		model.addAttribute("movieImageList", movieImageList);
		
		// 상영영화관
		List<CinemaDTO> cinemaShowingList = cinemadao.cinemaShowingList(no);
		model.addAttribute("cinemaShowingList", cinemaShowingList);

		// 리뷰
		List<ReviewDTO> reviewList = reviewdao.reviewList(no);
		model.addAttribute("reviewList", reviewList);
		
		// 리뷰 차트
		int[] score_count = new int[5];
		Arrays.fill(score_count, 0);
		for (ReviewDTO list : reviewList) {
			score_count[5 - (int) Math.floor(list.getReview_rate())] += 1;
		}
		model.addAttribute("score_count", score_count);
		
		// 연령별 예매율 , 성별 예매율
		int[] age_count = new int[5];
		Arrays.fill(age_count, 0);
		
		int[] gender_count = new int[2];
		Arrays.fill(gender_count, 0);
		
		List<MemberDTO> ticketMemberList = ticketdao.ticketMemberSelect(no);
		for (MemberDTO member: ticketMemberList) {
			int age = getAgeFromBirthday(member.getBirth());
			age = age / 10 * 10;
			if (age < 60) {
				age_count[(age - 1) / 10] += 1;
			}
			
			if (member.getGender().equals("M")) {
				gender_count[0] += 1;
			} else {
				gender_count[1] += 1;
			}
		}
		
		model.addAttribute("age_count", age_count);
		model.addAttribute("gender_count", gender_count);
		
		return "movie_detail";
	}
	
	// 생일로 나이 반환 함수
	public static int getAgeFromBirthday(String dateStr) throws ParseException {
	    Calendar birth = new GregorianCalendar();
	    Calendar today = new GregorianCalendar();
	    
	    // String to date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(dateStr);
        
	    birth.setTime(date);
	    today.setTime(new Date());

	    int factor = 0;
	    if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
	        factor = -1;
	    }
	    return today.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + factor;
	}
	
	// 영화 예매 페이지 
	@RequestMapping(value="/ticket", method=RequestMethod.GET)
	public String ticket(@RequestParam(value="movie", required=false) String movie, 
			@RequestParam(value="cinema", required=false) String cinema, Model model, HttpSession httpss) { 
		cinemaMenu(model);
		
		List<MovieDTO> ticketMovieList = ticketdao.ticketMovieList();
		model.addAttribute("ticketMovieList", ticketMovieList);
		
		List<CinemaDTO> ticketCinemaList = ticketdao.ticketCinemaList();
		model.addAttribute("ticketCinemaList", ticketCinemaList);
		
		LocalDate now = LocalDate.now(); 
		int year = now.getYear(); 
		int month = now.getMonth().getValue(); 
		int day = now.getDayOfMonth(); 

		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("day", day);
		
		// 선택된 영화나 영화관이 있을 경우, 넘어온 값이 없으면 null
		model.addAttribute("selectedMovieID", movie);
		model.addAttribute("selectedCinemaID", cinema);

		return "ticket";
	}
	
	// 영화예매 - 영화 상영시간대  
	@RequestMapping(value="/cinema_showing_action", method=RequestMethod.GET)
	public @ResponseBody List<ShowingTimeDTO> cinema_showing_action(String title_ko, String cinema_name, String showing_date) {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title_ko",  title_ko);
		map.put("cinema_name",  cinema_name);
		map.put("showing_date", showing_date);
		List<ShowingTimeDTO> ticketShowingTimeList = ticketdao.ticketShowingTimeList(map);
		
		if (ticketShowingTimeList == null) {
			System.out.println("정보가 없습니다.");
		} else {
			LocalDate now = LocalDate.now(); 
			int nowDay = now.getDayOfMonth(); 
			int showingDay = Integer.parseInt(showing_date.substring(8, 10));
			LocalTime nowTime = LocalTime.now(); 
			int nowHour = nowTime.getHour();
			int nowMin = nowTime.getMinute();
			
			if (nowDay == showingDay) { 
				List<ShowingTimeDTO> removeList = new ArrayList<ShowingTimeDTO>();
				
				for (ShowingTimeDTO s : ticketShowingTimeList) {
					int startHour = Integer.parseInt(s.getShowing_time().substring(0, 2));
					int startMin = Integer.parseInt(s.getShowing_time().substring(3, 5));
					
					if (startHour < nowHour || (startHour == nowHour && startMin <= nowMin)) {
						removeList.add(s);
					}
				}
				for (ShowingTimeDTO s: removeList) {
					System.out.println(s);
					ticketShowingTimeList.remove(s);
				}
			}
		}
		return ticketShowingTimeList;
	}
	
	// 영화 예매 - 좌석 정보 
	@RequestMapping(value="/seat_infor_action", method=RequestMethod.GET)
	public @ResponseBody String seat_infor_action(int cinema_seat_id, int cinema_showing_id) {

		SeatInforDTO ticketSeatInfor = ticketdao.ticketSeatInfor(cinema_seat_id);
		List<String> reservedSeats = ticketdao.reservedSeats(cinema_showing_id);
		
		JsonObject seatResult = new JsonObject();
		Gson gson = new Gson();
		seatResult.addProperty("SeatInfor", gson.toJson(ticketSeatInfor));
		seatResult.addProperty("reservedSeats", gson.toJson(reservedSeats));
		String result = gson.toJson(seatResult);
		return result;
	}
	
	// 영화 예매 - 로그인 여부 확인
	@RequestMapping(value="/login_check", method=RequestMethod.GET)
	public @ResponseBody String login_check(HttpSession httpss) {
		
		String id = (String) httpss.getAttribute("myinfo");
		if (id == null) {
			return "0";
		} else {
			return id;
		}
	}
	
	// 영화 예매 - 결제 정보 저장
	@RequestMapping(value="/pay_action", method=RequestMethod.GET)
	public @ResponseBody void pay_action(String cinema_showing_id, int youth, int adult, String seat, int price, HttpSession httpss) {
		
		String member_id = (String) httpss.getAttribute("myinfo");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("member_id",  member_id);
		map.put("cinema_showing_id",  cinema_showing_id);
		map.put("youth", youth);
		map.put("adult", adult);
		map.put("seat", seat);
		map.put("price", price);
		ticketdao.ticketInsert(map);
	}

}