<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	request.setCharacterEncoding("UTF-8");
String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ManagerMemberList</title>
<link rel="stylesheet" type="text/css" href="css/bootstrap.css" />

<style>
/* 검색창 스타일 */
form {
	display: flex;
	max-width: 300px;
}

input[type="text"] {
	padding: 10px;
	border: 1px solid #ccc;
	border-radius: 5px;
	flex: 1;
	height: 40px;
}

input[type="button"] {
	background-color: #007bff;
	color: white;
	border: none;
	border-radius: 5px;
	padding: 10px;
	cursor: pointer;
	height: 40px;
	cursor: pointer;
}

input[type="button"]:hover {
	background-color: #0056b3;
}
</style>
</head>
<body>
	<div style="margin-left: -10%; width: 120%;">
		<div>
			<h2 style="margin-top: 20px;">회원관리</h2>
			<hr>
		</div>

		<div>
			<form>
				<input type="text" placeholder="아이디 검색" id="userId"> <input
					type="button" value="검색" onclick="searchAjax()">
			</form>
		</div>

		<div id="completeTable" class="mt-4">
			<table class="table table-bordered table-hover">
				<thead>
					<tr>
						<th style="color: black;">접수날짜</th>
						<th style="color: black;">펫시터 등급</th>
						<th style="color: black;">펫시터 닉네임</th>
						<th style="color: black;">펫시팅 글 이름</th>
						<th style="color: black;">신고사유</th>
						<th style="color: black;">신고조치</th>
						<th style="color: black;">신고처리한 관리자</th>
						<th style="color: black;">처리날짜</th>
					</tr>
				</thead>
				<tbody>
					<%
						// 임의의 데이터 생성
					String[][] data = { { "2023-08-01", "Gold", "PetLover123", "Summer Fun", "불적절한 언어 사용", "경고", "Admin1", "2023-08-02" },
							{ "2023-08-02", "Silver", "AnimalLover", "Loving Paws", "매너 불이행", "휴면 계정", "Admin2", "2023-08-03" },
							{ "2023-08-03", "Bronze", "CaringPal", "Cozy Retreat", "사기행위 및 도주 시도", "강제 탈퇴", "Admin3", "2023-08-04" },
							{ "2023-08-04", "Platinum", "PawsomeCare", "Playful Pets", "신고 무시 및 협박", "계정 정지", "Admin4", "2023-08-05" },
							{ "2023-08-05", "Gold", "PetWatchdog", "Lovely Comps", "반려동물 유기 및 방치", "계정 삭제", "Admin5", "2023-08-06" } };

					// 데이터 출력
					for (String[] row : data)
					{
					%>
					<tr>
						<td><%=row[0]%></td>
						<td><%=row[1]%></td>
						<td><%=row[2]%></td>
						<td><%=row[3]%></td>
						<td><%=row[4]%></td>
						<td><%=row[5]%></td>
						<td><%=row[6]%></td>
						<td><%=row[7]%></td>
					</tr>
					<%
						}
					%>
				</tbody>
			</table>
		</div>

	</div>

</body>
</html>