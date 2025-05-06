-- 1. 사용자 테이블 (질병 필드 제거)
CREATE TABLE `users`
(
    `id`            INT          NOT NULL AUTO_INCREMENT,
    `login_id`      VARCHAR(30)  NOT NULL UNIQUE COMMENT '로그인 아이디',
    `password_hash` VARCHAR(100) NOT NULL COMMENT '해시된 비밀번호',
    `name`          VARCHAR(50)  NOT NULL COMMENT '실명',
    `birth_date`    DATE         NOT NULL COMMENT '생년월일',
    `gender`        ENUM('M','F')   NOT NULL COMMENT '성별',
    `email`         VARCHAR(100) UNIQUE COMMENT '이메일',
    `height`        DECIMAL(5, 2) NULL COMMENT '키(cm)',
    `weight`        DECIMAL(5, 2) NULL COMMENT '몸무게(kg)',
    `theme_pref`    ENUM('default','senior') NOT NULL DEFAULT 'senior'
                                  COMMENT 'UI 테마(default, 고령자용)',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 질병 마스터 테이블
CREATE TABLE `diseases`
(
    `id`          INT          NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL UNIQUE COMMENT '질병명',
    `description` VARCHAR(255) NULL COMMENT '설명',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 사용자-질병 매핑 테이블 (다중 질병 선택 지원)
CREATE TABLE `user_diseases`
(
    `user_id`      INT      NOT NULL,
    `disease_id`   INT      NOT NULL,
    `diagnosed_at` DATE NULL COMMENT '진단일',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `disease_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`disease_id`) REFERENCES `diseases` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 가족 그룹 테이블
CREATE TABLE `groups`
(
    `id`         INT      NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(100) NULL COMMENT '그룹 이름',
    `code`       CHAR(36) NOT NULL UNIQUE COMMENT 'UUID 그룹 코드',
    `created_by` INT      NOT NULL COMMENT '생성자 user_id',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 사용자-그룹 매핑 (보호자 구분 없음)
CREATE TABLE `user_groups`
(
    `user_id`   INT      NOT NULL,
    `group_id`  INT      NOT NULL,
    `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `group_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 식단(식사 기록) 테이블
CREATE TABLE `meals`
(
    `id`         INT      NOT NULL AUTO_INCREMENT,
    `user_id`    INT      NOT NULL COMMENT '등록자 user_id',
    `eat_date`   DATE     NOT NULL COMMENT '식사 날짜',
    `time_slot`  ENUM('아침','점심','저녁','간식') NOT NULL COMMENT '시간대',
    `photo_url`  VARCHAR(255) NULL COMMENT '음식 사진 URL',
    `food_name`  VARCHAR(100) NULL COMMENT '음식 명칭',
    `memo`       VARCHAR(255) NULL COMMENT '메모',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`user_id`, `eat_date`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 식단 댓글 테이블
CREATE TABLE `meal_comments`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `meal_id`    INT          NOT NULL COMMENT 'mealDtos.id',
    `user_id`    INT          NOT NULL COMMENT '댓글 작성자 user_id',
    `content`    VARCHAR(500) NOT NULL COMMENT '댓글 내용',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`meal_id`, `created_at`),
    FOREIGN KEY (`meal_id`) REFERENCES `meals` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 복약(약 정보) 테이블 (복약 대상자 필드 제거)
CREATE TABLE `medications`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `user_id`    INT          NOT NULL COMMENT '사용자 ID',
    `med_date`   DATE         NOT NULL COMMENT '복약 날짜',
    `time_slot`  ENUM('아침','점심','저녁') NOT NULL COMMENT '시간대',
    `drug_name`  VARCHAR(100) NOT NULL COMMENT '약 이름',
    `photo_url`  VARCHAR(255) NULL COMMENT '약봉투 사진 URL',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`user_id`, `med_date`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. 복약 알림 설정 테이블
CREATE TABLE `medication_reminders`
(
    `id`            INT      NOT NULL AUTO_INCREMENT,
    `medication_id` INT      NOT NULL COMMENT 'medications.id',
    `reminder_type` ENUM('식전','식후','취침전') NOT NULL COMMENT '알림 타입',
    `is_enabled`    TINYINT(1)        NOT NULL DEFAULT 1 COMMENT '알림 사용 여부',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`medication_id`),
    FOREIGN KEY (`medication_id`) REFERENCES `medications` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. 건강 Tip 테이블
CREATE TABLE `tips`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `content`    VARCHAR(500) NOT NULL COMMENT '건강 팁 내용',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. 질병-팁 연결 테이블 (다중 질병 관련 팁 지원)
CREATE TABLE `disease_tips`
(
    `disease_id` INT NOT NULL,
    `tip_id`     INT NOT NULL,
    PRIMARY KEY (`disease_id`, `tip_id`),
    FOREIGN KEY (`disease_id`) REFERENCES `diseases` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tip_id`) REFERENCES `tips` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. 사용자-팁 노출 이력 테이블
CREATE TABLE `user_tips`
(
    `user_id`  INT      NOT NULL,
    `tip_id`   INT      NOT NULL,
    `shown_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '노출 시각',
    PRIMARY KEY (`user_id`, `tip_id`, `shown_at`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tip_id`) REFERENCES `tips` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. 알림 로그 테이블
CREATE TABLE `medication_reminders`
(
    `id`            INT      NOT NULL AUTO_INCREMENT,
    `medication_id` INT      NOT NULL COMMENT 'medications.id',
    `reminder_type` ENUM('식전','식후','취침전') NOT NULL COMMENT '알림 타입',
    `is_enabled`    TINYINT(1)        NOT NULL DEFAULT 1 COMMENT '알림 사용 여부',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`medication_id`),
    FOREIGN KEY (`medication_id`) REFERENCES `medications` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. 주간 건강 리포트 테이블
CREATE TABLE `weekly_reports`
(
    `id`            INT           NOT NULL AUTO_INCREMENT,
    `user_id`       INT           NOT NULL COMMENT '사용자 ID',
    `start_date`    DATE          NOT NULL COMMENT '시작 날짜',
    `end_date`      DATE          NOT NULL COMMENT '종료 날짜',
    `meal_count`    INT           NOT NULL DEFAULT 0 COMMENT '식사 횟수',
    `med_adherence` DECIMAL(5, 2) NOT NULL DEFAULT 0.00 COMMENT '복약 준수율(%)',
    `health_score`  INT NULL COMMENT '건강 점수',
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`user_id`, `start_date`, `end_date`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. 식단 추천 테이블
CREATE TABLE `diet_recommendations`
(
    `id`         INT      NOT NULL AUTO_INCREMENT,
    `report_id`  INT      NOT NULL COMMENT '주간 리포트 ID',
    `content`    TEXT     NOT NULL COMMENT '추천 내용',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`report_id`) REFERENCES `weekly_reports` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. 세션 관리 테이블 (JWT 토큰)
CREATE TABLE `sessions`
(
    `id`            INT          NOT NULL AUTO_INCREMENT,
    `user_id`       INT          NOT NULL,
    `refresh_token` VARCHAR(255) NOT NULL COMMENT '리프레시 토큰',
    `expires_at`    DATETIME     NOT NULL COMMENT '만료 시간',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`refresh_token`),
    INDEX (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. 초기 데이터: 질병 마스터 데이터 삽입
INSERT INTO `diseases` (`name`, `description`)
VALUES ('없음', '해당 없음'),
       ('당뇨', '혈당 조절에 문제가 있는 질환'),
       ('고혈압', '혈압이 정상 범위보다 높은 상태'),
       ('신장질환', '신장 기능에 문제가 있는 질환'),
       ('심장질환', '심장 기능에 문제가 있는 질환'),
       ('관절염', '관절에 염증이 생기는 질환'),
       ('기타', '기타 질환');

show databases ;
show tables ;
select * from users;

select * from meals;

insert into users (login_id, password_hash, name, birth_date, gender, email, height, weight, theme_pref)
values ('ssafy111', 'ssafy', '김싸피', '2025-05-03', 'M', 'ssafy111@naver.com', 170.2, 70.2, 'default');

insert into users (login_id, password_hash, name, birth_date, gender, email, height, weight)
values ('ssafy2', 'ssafy2', '김싸피2', '2025-05-03', 'M', 'ssafy2@naver.com', 170.2, 70.2);

INSERT INTO meals (user_id, eat_date, time_slot, food_name, memo)
VALUES (1, '2025-05-03', '아침', '돈까스', '오제제');

ALTER TABLE users
    ADD COLUMN diabetes TINYINT(1) DEFAULT 0,
    ADD COLUMN hypertension TINYINT(1) DEFAULT 0,
    ADD COLUMN hyperlipidemia TINYINT(1) DEFAULT 0,
    ADD COLUMN heart_disease TINYINT(1) DEFAULT 0,
    ADD COLUMN kidney_disease TINYINT(1) DEFAULT 0,
    ADD COLUMN allergies TINYINT(1) DEFAULT 0,
    ADD COLUMN family_code VARCHAR(50) NULL;