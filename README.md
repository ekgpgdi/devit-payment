# Devit
<p align="center"> 
<img src = 'https://user-images.githubusercontent.com/84092014/177942862-e4755aa7-f87b-4eaa-8eae-07bcaeb3932e.png' style='width:300px;'/>
</p>
경험이 많고 실력 있는 개발자에게 도움을 받기 위한 플랫폼입니다. <br/>
기업 또는 개인에게 알맞는 개발자의 스펙과 원하는 직무를 등록하여 구인하고 개발자는 확인 후 지원서를 넣어 서로가 만족하는 상황이 되었을 때 계약이 진행될 수 있도록 중개하는 웹 사이트입니다. <br/>

## Devit Architecture
<img width="1005" alt="스크린샷 2022-07-25 오후 12 34 59" src="https://user-images.githubusercontent.com/84092014/180694026-b0c51181-5ddc-4e84-b659-2d32d33e05eb.png">

## Devit Payment Service
Devit 프로젝트 내 결제 서비스입니다. <br/>
인증 서비스에서 회원가입이 진행되면 인증 서비스에서 포인트 서비스로 RabbitMQ를 통하여 메세지를 전달하고 포인트 서비스는 해당 메세지를 받아 해당 유저와 매핑되는 point 정보를 생성합니다. <br/>
포인트를 충전하고 해당 포인트로 결제를 진행합니다. <br/>

[결제 시나리오]
1. 결제에 대한 요청이 들어옴
2. 제공자, 구매자와 매핑되는 point 테이블을 확인
   * 한 명이라도 매핑되는 정보가 없으면 404 에러
3. Board 도메인으로 uid 로 게시물에 대한 정보 요청 (API 통신)
   * 이때 받아온 게시물의 price 보다 구매자의 보유 포인트가 적으면 400 에러
4. 구매자의 포인트는 감소 및 포인트 기록 추가
5. 제공자의 포인트는 증가 및 포인트 기록 추가
6. 결제 기록 추가

## link to another repo
eureka server : https://github.com/ekgpgdi/devit-eureka-server  <br/>
gateway : https://github.com/ekgpgdi/devit-gateway <br/>
certification : https://github.com/ekgpgdi/devit-certification-service <br/>
board : https://github.com/kimziaco/devit-board <br/>
user : https://github.com/eet43/devit-user <br/>
chat : https://github.com/eet43/devit-chat <br/>
