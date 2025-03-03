package com.act.sitting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class SittingController
{
	@Autowired
	private ISittingService sittingService;

	// 펫시팅(돌봄장소) 리스트업 페이지로 가기
	@RequestMapping("/sittinglistpage.action")
	public String sittingList(Model model)
	{
		String view = "";
		
		
		model.addAttribute("list", sittingService.list());
		model.addAttribute("photoList", sittingService.sittingPlacePhoto());
		model.addAttribute("IndexTagList", sittingService.IndexTagList());
		model.addAttribute("tagList", sittingService.tagList());
		model.addAttribute("sittingSrwRates", sittingService.sittingSrwRates());
		
		view = "/WEB-INF/views/sitting/SittingListPage.jsp";
		return view;
	}
	
	

	// 펫시터 예약화면 데이터 바인딩
	@RequestMapping("/sittingreservationpage.action")
	public String getSittingReservationView(String memSid, HttpSession session, Model model)
	{
		String result = "";
		
		SittingDTO list = sittingService.listPublicByMemSid(memSid);
		String pMemSid = (String)session.getAttribute("memSid");
		SittingDTO photos = sittingService.sittingPlacePhotoBySpsid(list.getSpSid());
		
		ArrayList<SittingDTO> spListTags = sittingService.sittingPlaceTagsByMemSid(memSid);
		ArrayList<SittingDTO> spRest = sittingService.spRest(memSid);
		ArrayList<SittingDTO> reviews = sittingService.sittingReviews(memSid);
		ArrayList<SittingDTO> reviewsPhoto = sittingService.sittingReviewsPhoto();
		SittingDTO sittingSrwRate = sittingService.sittingSrwRate(memSid);
		ArrayList<SittingDTO> petList = sittingService.petListByMemSid(pMemSid);
		ArrayList<SittingDTO> sbList = sittingService.sbDates(memSid);
		
		ArrayList<SittingDTO> irList = new ArrayList<SittingDTO>();
		irList = sittingService.indexReport();
		
		model.addAttribute("irList", irList);
		
		model.addAttribute("list", list);
		model.addAttribute("spListTags", spListTags);
		model.addAttribute("spRest", spRest);
		model.addAttribute("reviews", reviews);
		model.addAttribute("reviewsPhoto", reviewsPhoto);
		model.addAttribute("sittingSrwRate", sittingSrwRate);
		model.addAttribute("petList", petList);
		model.addAttribute("sbList", sbList);
		model.addAttribute("photos", photos);
		
		//System.out.println( list.getSphEnd().split(":")[0] );
		int ch = Integer.valueOf(list.getSphStart().split(":")[0]);
		ch = ch - 2;
		
		String checkOut = String.valueOf(ch);
		checkOut = checkOut + ":00";
		
		model.addAttribute("checkOut", checkOut);
		
		result = "/WEB-INF/views/sitting/SittingReservationPage.jsp";
		
		return result;
	}
	
	
	@RequestMapping("/sittingbooking.action")
	public ModelAndView sittingBooking(SittingDTO dto, HttpSession session,Model model,HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView();
		
		String pMemSid = (String)session.getAttribute("memSid");	// 견주의 회원번호
		String datePicker1 = dto.getDatepicker1();
		String datePicker2 = dto.getDatepicker2();
		String selectedPets = dto.getSelectedPets();
		int pricee = dto.getPricee();
		
		//System.out.println("pMemSid: " + pMemSid);
		//System.out.println("datePicker1: " + datePicker1);
		//System.out.println("datePicker2: " + datePicker2);
		//System.out.println("selectedPets: " + selectedPets);
		//System.out.println("pricee: " + pricee);
		
		dto.setPrice(pricee);			
		dto.setpMemSid(pMemSid);		 
		dto.setSbStart(datePicker1);
		dto.setSbEnd(datePicker2);
		
		
		String[] selectedPetString = selectedPets.split(",");
		  
		List<Integer> selectedPetsSid = new ArrayList<>();
		  
		for(String str : selectedPetString)
		{
			int value = Integer.parseInt(str);
			selectedPetsSid.add(value);
		}
		
		dto.setSelectedPetsSid(selectedPetsSid);
		
		int result = sittingService.sittingFromCreateCartToBook(dto);
		//System.out.println("서비스 다녀온 result : " + result);
		
		
		String memSid = (String)session.getAttribute("memSid");
		
		String nick = request.getParameter("nick");
		String grade = request.getParameter("grade");
		String rate = request.getParameter("rate");
		String count = request.getParameter("count");

		mv.addObject("nick", nick);
		mv.addObject("grade", grade);
		mv.addObject("rate", rate);
		mv.addObject("count", count);
		
		mv.setViewName("redirect:booksuccess.action");
		return mv;
	}
	
	
	
	@RequestMapping("/booksuccess.action")
	public ModelAndView bookSuccess(HttpServletRequest request, Model model, HttpSession session) 
	{
		ModelAndView mv = new ModelAndView();
		
		String memSid = (String)session.getAttribute("memSid");

		int num = sittingService.getReservationNum(memSid);
		//System.out.println(sittingService.getInfo(memSid, num));
		
		/*
		 * // 견주라는 것 if(memSid == memSid2) {
		 */
		
		mv.addObject("stlpList", sittingService.stlpList(num));
		
		mv.addObject("num", num);
		//System.out.println(num);
		mv.addObject("petList", sittingService.getInfo(memSid, num));
		mv.addObject("dto", sittingService.getMatchingHistory(memSid, num));
		
		String nick = request.getParameter("nick");
		String grade = request.getParameter("grade");
		String rate = request.getParameter("rate");
		String count = request.getParameter("count");
		String price = request.getParameter("price");
		
		
		mv.addObject("nick", nick);
		mv.addObject("grade", grade);
		mv.addObject("rate", rate);
		mv.addObject("count", count);
		mv.addObject("price", price);
		
		
		
		/*
		 } else {
		  
		 }
		 */
		
		mv.setViewName("/WEB-INF/views/index/ReservationInfo.jsp");
		return mv;
	}
	
	
	
	// 마이페이지 펫시팅. AJAX로 처리. 들어온 예약 확인하기
	@RequestMapping("/mypagesittingform.action")
	public String myPageSitting(HttpSession session ,Model model)
	{
		String view = "";
		SittingDTO dto = new SittingDTO();
		
		String memSid = (String)session.getAttribute("memSid");
		
		dto.setMemSid(memSid);
		
		int stsCount = sittingService.sittingStsCount(dto); // 시험제출번호
		int slCount = sittingService.sittingSlCount(dto); // 펫시팅면허번호
		
		
		//System.out.println(stsCount);
		//System.out.println(slCount);
		
		if(stsCount!=0)			// 시험제출번호를 가지고 있다면
		{
			if(slCount!=0)		// 펫시팅면허번호를 가지고 있다면
	        {
				if( sittingService.spCount(dto) != 0)	// 펫시팅면허번호를 가지고 있고, 돌봄장소를 가지고 있는 경우
				{
					// 현재 운영중인 돌봄장소번호
					int spSid = sittingService.sittingPlaceBasic(memSid).get(0).getSpSid();
					
					// 현재 운영중인 돌봄장소의 기본정보(태그, 사진, 휴무일 제외)
					model.addAttribute("info", sittingService.sittingPlaceBasic(memSid).get(0));
					
					// 현재 운영중인 돌봄장소의 특이사항
					model.addAttribute("tags", sittingService.sittingPlaceTags(spSid));
					
					// 나에게 달린 후기
					model.addAttribute("reviews", sittingService.sittingReviews(memSid));
					
					// 후기를 쓴 사람의 닉네임을 조회하기위한 전체 출력
					model.addAttribute("reviewers", sittingService.sittingReviewers());
					
					
					view = "/WEB-INF/ajax/MyPageSittingForm.jsp";
				}
				else	// 펫시팅면허번호를 가지고 있지만, 돌봄장소는 없는 경우
				{
					int key = 1;
					model.addAttribute("key", key);
					model.addAttribute("tags", sittingService.IndexTagList());
					
					// 돌봄장소 등록하기
					view = "/WEB-INF/ajax/MyPageSittingPlaceInsertForm.jsp";
				}
				
	        }
			else				// 시험제출번호를 가지고 있지만 , 면허번호가 없는 경우 (임시로 메인으로 보내버림)
				view = "redirect:mainpage.action";
		}
		else					// 시험을 보지 않은 회원인 경우	 (보내지는 페이지 수정 필요)
			view = "/WEB-INF/ajax/MyPageSittingRegisterForm.jsp";
	
		return view;
	}
	
	
	// 마이페이지 펫시팅의 돌봄장소 수정하기위해 오는 수정 페이지
	@RequestMapping("/updatesittingplac.action")
	public String updateSP(HttpSession session,Model model)
	{
		
		String result = "";
		String memSid = (String)session.getAttribute("memSid");
		
		// 현재 운영중인 돌봄장소번호
		int spSid = sittingService.sittingPlaceBasic(memSid).get(0).getSpSid();
		
		// 현재 운영중인 돌봄장소의 기본정보(태그, 사진, 휴무일 제외)
		model.addAttribute("info", sittingService.sittingPlaceBasic(memSid).get(0));
		
		// 현재 운영중인 돌봄장소의 특이사항
		model.addAttribute("myTags", sittingService.sittingPlaceTags(spSid));
		
		// 돌봄장소의 특이사항 리스트
		model.addAttribute("tagList", sittingService.IndexTagList());
		
		// 나에게 달린 후기
		model.addAttribute("reviews", sittingService.sittingReviews(memSid));
		
		// 후기를 쓴 사람의 닉네임을 조회하기위한 전체 출력
		model.addAttribute("reviewers", sittingService.sittingReviewers());
		
		
		result = "/WEB-INF/ajax/UpdateSittingPlaceForm.jsp";
		return result;
	}

	
	// 마이페이지에서 펫시팅의 돌봄장소 등록하기를 눌렀을 때
	@RequestMapping(value="/insertPlace.action", method = RequestMethod.POST)
	public String placeInsert(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException, ServletException
	{
		String result = "";
		
		// 1. submit해서 받아온 값들을 sittingDTO에 모두 담기
		SittingDTO dto = new SittingDTO();
		String fileName = "";
		String filePath = "";
		
		response.setContentType("text/html; charset=UTF-8");
		//PrintWriter out = response.getWriter();
		
		
		File attachesDir = new File("./WebContent/images/");
		
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
	    fileItemFactory.setRepository(attachesDir);
	    fileItemFactory.setSizeThreshold(1024 * 1024);
	    ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
	    
	    try {
            List<FileItem> items = fileUpload.parseRequest(request);
            for (FileItem item : items) {
              
                if (item.isFormField()) {        
                    if(item.getFieldName().equals("sptitle"))
                    	dto.setSptitle(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spContent")) 
                    	dto.setSpContent(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spMaxPet"))
                    	dto.setSpMaxPet(Integer.parseInt(item.getString("UTF-8")));
                    else if(item.getFieldName().equals("spAddr1"))
                    	dto.setSpAddr1(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spAddr2"))
                    	dto.setSpAddr2(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("extraAddr"))
                    	dto.setExtraAddr(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spZipCode"))
                    	dto.setSpZipCode(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("sphStart"))
                    	dto.setSphStart(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("sphEnd"))
                    	dto.setSphEnd(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("selectedTags"))
                    {
                    	String selectedTagsString = item.getString("UTF-8");
                    	List<String> selectedTags = Arrays.asList(selectedTagsString.split(","));	
                    	dto.setSelectedTags(selectedTags);
                    }
                    else if(item.getFieldName().equals("ipSid"))
                    	dto.setIpSid(Integer.parseInt(item.getString("UTF-8")));
                    
                } else {
              
                    if (item.getSize() > 0) {
                        String separator = File.separator;
                        int index =  item.getName().lastIndexOf(separator);
                        fileName = item.getName().substring(index  + 1);
                        filePath = "./WebContent/images/" + separator + fileName;
                        File uploadFile = new File(filePath);
                        item.write(uploadFile);
                        
                        if(item.getFieldName().equals("file"))
                        	dto.setSppName(item.getName()); // 파일 이름 설정
                       
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    // submit해서 받아온 값 외에 돌봄장소 등록에 필요한 값들을 추가적으로 dto에 set
	    String memSid = (String)session.getAttribute("memSid");
	    dto.setMemSid(memSid);
	    dto.setSppPath("./WebContent/images/");					// 사진을 저장한 폴더경로
	    dto.setSlSid(sittingService.slSid(memSid));
	    
	    // spSid 제외하고 인서트에 필요한 모든 값을 dto에 담았음
	    
	    //System.out.println(dto.getSpSid() + " / " + dto.getSptitle() + " / " + dto.getSpContent() + " / " +   dto.getSpAddr1()
	   // + " / " + dto.getSpAddr2() + " / " + dto.getSpZipCode() + " / " + dto.getSpMaxPet() + " / " 
	   // + " / " + dto.getIpSid() + " / " + dto.getExtraAddr() );
	    
	    // sitting_place 에 인서트, sitting_place_hub에 인서트, 이미지인서트, 특이사항인서트
	    boolean success = sittingService.insertPlcae(dto);
		
	    // insert 성공시엔 flag 에 0, 실패 후에는 flag에 1을 담아서 마이페이지를 리다이렉트
	   if(success)
		   result = "redirect:mypage.action?flag=0" ;
	   else
		   result = "redirect:mypage.action?flag=1" ;
	   
		return result;
	}
	
	
	// 돌봄장소 수정하기를 눌렀을 때 
	@RequestMapping(value="/updatePlace.action", method = RequestMethod.POST)
	public String placeUpdate(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException, ServletException
	{
		String result = "";
		
		// 1. submit해서 받아온 값들을 sittingDTO에 모두 담기
		SittingDTO dto = new SittingDTO();
		String fileName = "";
		String filePath = "";
		
		response.setContentType("text/html; charset=UTF-8");
		//PrintWriter out = response.getWriter();
		
		File attachesDir = new File("./WebContent/images/");
		
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
	    fileItemFactory.setRepository(attachesDir);
	    fileItemFactory.setSizeThreshold(1024 * 1024);
	    ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
	    
	    try {
            List<FileItem> items = fileUpload.parseRequest(request);
            for (FileItem item : items) {
              
                if (item.isFormField()) {        
                    if(item.getFieldName().equals("sptitle"))
                    	dto.setSptitle(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spContent")) 
                    	dto.setSpContent(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spMaxPet"))
                    	dto.setSpMaxPet(Integer.parseInt(item.getString("UTF-8")));
                    else if(item.getFieldName().equals("spAddr1"))
                    	dto.setSpAddr1(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spAddr2"))
                    	dto.setSpAddr2(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("extraAddr"))
                    	dto.setExtraAddr(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("spZipCode"))
                    	dto.setSpZipCode(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("sphStart"))
                    	dto.setSphStart(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("sphEnd"))
                    	dto.setSphEnd(item.getString("UTF-8"));
                    else if(item.getFieldName().equals("selectedTags"))
                    {
                    	String selectedTagsString = item.getString("UTF-8");
                    	List<String> selectedTags = Arrays.asList(selectedTagsString.split(","));	
                    	dto.setSelectedTags(selectedTags);
                    }
                    else if(item.getFieldName().equals("ipSid"))
                    	dto.setIpSid(Integer.parseInt(item.getString("UTF-8")));
                    else if(item.getFieldName().equals("spSid"))
                    	dto.setSpSid(Integer.parseInt(item.getString("UTF-8")));
                    else if(item.getFieldName().equals("sphSid"))
                    	dto.setSphSid(Integer.parseInt(item.getString("UTF-8")));
                    
                } else {
              
                    if (item.getSize() > 0) {
                        String separator = File.separator;
                        int index =  item.getName().lastIndexOf(separator);
                        fileName = item.getName().substring(index  + 1);
                        filePath = "./WebContent/images/" + separator + fileName;
                        File uploadFile = new File(filePath);
                        item.write(uploadFile);
                        
                        if(item.getFieldName().equals("file"))
                        	dto.setSppName(item.getName()); // 파일 이름 설정
                       
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    // submit해서 받아온 값 외에 돌봄장소 등록에 필요한 값들을 추가적으로 dto에 set
	    String memSid = (String)session.getAttribute("memSid");
	    dto.setMemSid(memSid);
	    dto.setSppPath("./WebContent/images/");					// 사진을 저장한 폴더경로
	    dto.setSlSid(sittingService.slSid(memSid));
	    
	    // spSid 제외하고 업데이트에 필요한 모든 값을 dto에 담았음
	    
	    //System.out.println(dto.getSpSid() + " / " + dto.getSpSid() + " / " + dto.getSptitle() + " / " + dto.getSpContent() + " / " +   dto.getSpAddr1()
	    //+ " / " + dto.getSpAddr2() + " / " + dto.getSpZipCode() + " / " + dto.getSpMaxPet() + " / " 
	    //+ " / " + dto.getIpSid() + " / " + dto.getExtraAddr() );
	    
	    // sitting_place 업데이트, sitting_place_hub  업데이트, 이미지 딜리트 후 인서트, 특이사항 딜리트 후 인서트
	    boolean success = sittingService.updatePlace(dto);
		
	    // update 성공/실패 후에는 flag에 2를 담아서 마이페이지를 리다이렉트
	    // 이 flag는 마이페이지에서 업데이트 alert을 띄워주기 위함이다.
	    if (success)
	    	result = "redirect:mypage.action?flag=2";
	    else
	    	result = "redirect:mypage.action?flag=3";
	    	
		return result;
	}
	
	
	// 펫시팅 리스트에서 펫시터 지원하기 눌렀을때
	@RequestMapping("/sittingtest.action")
	public String main(HttpSession session)
	{
		String result = "";
		
		String memSid = (String)session.getAttribute("memSid");
		SittingDTO dto = new SittingDTO();
		dto.setMemSid(memSid);
		int count = sittingService.sittingtestQualification(dto);
		
		if(count > 0)	
			result = "redirect:sittinglistpage.action?alertMessage=alreadyQualified";
		else
			result = "/WEB-INF/views/index/TermsPage.jsp";
		
		return result;
	}
	
	// 펫시터 약관 동의하고 테스트 페이지로 감
	@RequestMapping("/sittingteststart.action")
	public String openSittingTestStart(HttpSession session, Model model)
	{
		String result = "";
		
		// 약관동의 후 제출하기를 누르면 접수테이블 insert
		String memSid = (String)session.getAttribute("memSid");
		
		model.addAttribute("questionSittingList", sittingService.sittingTest(memSid));
		result = "/WEB-INF/views/index/TestPage.jsp";

		return result;
	}
	
	// 펫시터 시험을 보고 제출하기를 눌렀을 때
	@RequestMapping(value = "/submitQuiz.action", method = RequestMethod.POST)
	public String submitQuiz(@ModelAttribute SittingQuestionDTO dto, HttpServletRequest request, Model model, HttpSession session) 
	{
	    String answers = request.getParameter("answers");
	    String[] userAnswers = answers.split(",");
	    
	    String memSid = (String) session.getAttribute("memSid");	// 세션
	    
	    SittingQuestionDTO result = sittingService.gradeQuiz(userAnswers, memSid);	    
	    
	    boolean passed = result.getTotalScore() >= result.getSpcstandard();
	    
	    model.addAttribute("passed", passed);
	    model.addAttribute("nickName", result.getJmnickname());
	    model.addAttribute("totalScore", result.getTotalScore());
	    
	    String view = "/WEB-INF/views/index/TestResultPage.jsp";
	    return view;
	}
	
	@RequestMapping("/pay.action")
	public String proceedPayment()
	{
		String result = "";
		
		result = "/WEB-INF/views/index/ReservationPaymentPage.jsp";
		return result;
	
	}
	
	
	// 예약정보 확인 -> 견주일때
	@RequestMapping("/sittingreservationinfo.action")
	public String getReservation(HttpSession session, Model model, HttpServletRequest request,SittingDTO dto)
	{
		String result = "";
		
		String memSid = (String)session.getAttribute("memSid");
		
		int sbSid = Integer.parseInt(request.getParameter("sbSid"));
		
		// 예약번호로 산책 반려견 정보 조회
		model.addAttribute("bookPet",sittingService.bookPet(sbSid));
		
		// 예약번호로 펫시터, 돌봄장소 조회
		model.addAttribute("bookSitterPlace", sittingService.bookSitterPlace(sbSid));
		
		// 예약번호로 타임라인사진 조회
		model.addAttribute("stlpList", sittingService.stlpList(sbSid));
		
		result = "/WEB-INF/views/index/NewReservationInfo.jsp";
		
		return result;
	}
	
	
	// 펫시터 후기 모달
	@RequestMapping("/sittingreview.action")
	public String sittingReview(HttpServletRequest requset, Model model)
	{
		String result = "";

		int srwSid = Integer.parseInt(requset.getParameter("srwSid"));
		
		// 해당 버튼에 해당하는 후기 조회
		model.addAttribute("review", sittingService.sittingReview(srwSid));
		
		// 후기를 쓴 사람의 닉네임을 조회하기위한 전체 출력
		model.addAttribute("reviewers", sittingService.sittingReviewers());
		
		result = "/WEB-INF/views/sitting/SittingReviewPage.jsp";

		return result;
	}
	
	
	// 펫시팅 리스트 검색필터 버튼 (태그검색 포함)
	@RequestMapping("/sittingfilterlistform.action") 
	public String sittingFilterListForm(SittingDTO dto, Model model) 
	{ 
		String view = "";
		
		String[] isptSidList = dto.getIsptSidList().split(",");
		
		List<String> isptSidListString = new ArrayList<String>();
		
		for(String str : isptSidList)
		{
			isptSidListString.add(str);
		}
		
		dto.setIsptSidListSize(isptSidListString.size());
		dto.setIsptSidListString(isptSidListString);
		
		model.addAttribute("list", sittingService.sittingFilterList(dto));
		model.addAttribute("photoList", sittingService.sittingPlacePhoto());
		model.addAttribute("IndexTagList", sittingService.IndexTagList());
		model.addAttribute("tagList", sittingService.tagList());
		model.addAttribute("sittingSrwRates", sittingService.sittingSrwRates());
		
		model.addAttribute("searchAddr", dto.getSearchExtraAddr());
		model.addAttribute("searchDate", dto.getDatepicker());
		model.addAttribute("searchMaxPet", dto.getSpMaxPet());
		  
		  
		view = "/WEB-INF/views/sitting/SittingListPage.jsp";
		return view ;
	}
	 
	// 펫시팅 프로필 신고
	@RequestMapping("/profilereportreceive.action")
	public String profileReportReceive(Model model,SittingDTO dto)
	{
		String result = "";
		
		model.addAttribute("profileReportReceive", sittingService.profileReportReceive(dto));
		result = "redirect:sittinglistpage.action";
		return result;
	}
	
	// 펫시팅 돌봄공간 신고사유 리스트
	
	public String indexReport(Model model)
	{
		String result = "";
		ArrayList<SittingDTO> irList = new ArrayList<SittingDTO>();
		irList = sittingService.indexReport();
		
		model.addAttribute("irList", irList);
		
		return result;
	}
	
	// 펫시팅 돌봄공간 신고
	@RequestMapping("/sittingreportreceive.action")
	public String placeReportReceive(Model model, SittingDTO dto)
	{
		String result = "";
		model.addAttribute("placeReportReceive",sittingService.placeReportReceive(dto));
		
		result = "redirect:sittinglistpage.action";
		return result;
	}
	
	
	
	// <<미완>>
	// 펫시팅 리스트 검색필터 버튼 (태그검색 포함)
	/*
	 * @RequestMapping("/sittingfilterlistform.action") public String
	 * sittingFilterListForm(
	 * 
	 * @RequestParam("extraAddr") String extraAddr,
	 * 
	 * @RequestParam("datePicker") String datepicker,
	 * 
	 * @RequestParam("spMaxPet") int spMaxPet,
	 * 
	 * @RequestParam(value = "isptSidList[]", required = false) List<Integer>
	 * isptSidList, Model model) {
	 * 
	 * System.out.println("extraAddr: " + extraAddr);
	 * System.out.println("datepicker: " + datepicker);
	 * System.out.println("spMaxPet: " + spMaxPet);
	 * System.out.println("isptSidList: " + isptSidList); String view = "";
	 * 
	 * System.out.println("extraAddr: " + dto.getExtraAddr());
	 * System.out.println("datePicker: " + dto.getDatePicker());
	 * System.out.println("spMaxPet:" + dto.getSpMaxPet());
	 * System.out.println("isptSidList (request): " + list);
	 * System.out.println("isptSidList (dto) : " + dto.getIsptSidList());
	 * System.out.println("여기까지 옴1 이제 service간다");
	 * 
	 * //System.out.println("여기까지 옴2 이제 태그검색간다간다쑝간다");
	 * 
	 * System.out.println("진짜 되라고 이자식들아 extraAddr: " + extraAddr + "  datepicker: "
	 * + datepicker + "  spMaxPet: " + spMaxPet + " isptSidList: " + isptSidList);
	 * //model.addAttribute("filterlist", sittingService.sittingFilterList(dto));
	 * //model.addAttribute("filtertaglist",
	 * sittingService.sittingFilterTagList(dto));
	 * 
	 * 
	 * view = "/WEB-INF/ajax/sitting/SittingFilterListForm.jsp"; return view; }
	 */

}
