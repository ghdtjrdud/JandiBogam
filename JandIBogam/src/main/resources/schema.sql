-- 1. 사용자 테이블
CREATE TABLE `users`
(
    `id`             INT                       NOT NULL AUTO_INCREMENT,
    `login_id`       VARCHAR(30)               NOT NULL UNIQUE COMMENT '로그인 아이디',
    `password_hash`  VARCHAR(100)              NOT NULL COMMENT '해시된 비밀번호',
    `name`           VARCHAR(50)               NOT NULL COMMENT '실명',
    `birth_date`     DATE                      NOT NULL COMMENT '생년월일',
    `gender`         ENUM ('M','F')            NOT NULL COMMENT '성별',
    `email`          VARCHAR(100) UNIQUE COMMENT '이메일',
    `height`         DECIMAL(5, 2)             NULL COMMENT '키(cm)',
    `weight`         DECIMAL(5, 2)             NULL COMMENT '몸무게(kg)',
    `theme_pref`     ENUM ('default','senior') NOT NULL DEFAULT 'senior'
        COMMENT 'UI 테마(default, 고령자용)',
    `diabetes`       TINYINT(1)                         DEFAULT 0,
    `hypertension`   TINYINT(1)                         DEFAULT 0,
    `hyperlipidemia` TINYINT(1)                         DEFAULT 0,
    `heart_disease`  TINYINT(1)                         DEFAULT 0,
    `kidney_disease` TINYINT(1)                         DEFAULT 0,
    `allergies`      TINYINT(1)                         DEFAULT 0,
    `family_code`    VARCHAR(50)               NULL,
    `created_at`     DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 2. 질병 마스터 테이블
CREATE TABLE `diseases`
(
    `id`          INT          NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL UNIQUE COMMENT '질병명',
    `description` VARCHAR(255) NULL COMMENT '설명',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 3. 사용자-질병 매핑 테이블 (다중 질병 선택 지원)
CREATE TABLE `user_diseases`
(
    `user_id`      INT      NOT NULL,
    `disease_id`   INT      NOT NULL,
    `diagnosed_at` DATE     NULL COMMENT '진단일',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `disease_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`disease_id`) REFERENCES `diseases` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 4. 가족 그룹 테이블
CREATE TABLE `groups`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(100) NULL COMMENT '그룹 이름',
    `code`       CHAR(36)     NOT NULL UNIQUE COMMENT 'UUID 그룹 코드',
    `created_by` INT          NOT NULL COMMENT '생성자 user_id',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 5. 사용자-그룹 매핑 (보호자 구분 없음)
CREATE TABLE `user_groups`
(
    `user_id`   INT      NOT NULL,
    `group_id`  INT      NOT NULL,
    `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `group_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 6. 식단(식사 기록) 테이블
CREATE TABLE `meals`
(
    `id`         INT                        NOT NULL AUTO_INCREMENT,
    `user_id`    INT                        NOT NULL COMMENT '등록자 user_id',
    `eat_date`   DATE                       NOT NULL COMMENT '식사 날짜',
    `time_slot`  ENUM ('아침','점심','저녁','간식') NOT NULL COMMENT '시간대',
    `photo_url`  VARCHAR(255)               NULL COMMENT '음식 사진 URL',
    `food_name`  VARCHAR(100)               NULL COMMENT '음식 명칭',
    `memo`       VARCHAR(255)               NULL COMMENT '메모',
    `created_at` DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`user_id`, `eat_date`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 7. 식단 댓글 테이블
CREATE TABLE `meal_comments`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `meal_id`    INT          NOT NULL COMMENT 'meals.id',
    `user_id`    INT          NOT NULL COMMENT '댓글 작성자 user_id',
    `content`    VARCHAR(500) NOT NULL COMMENT '댓글 내용',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`meal_id`, `created_at`),
    FOREIGN KEY (`meal_id`) REFERENCES `meals` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 8. 복약(약 정보) 테이블
CREATE TABLE `medications`
(
    `id`         INT                   NOT NULL AUTO_INCREMENT,
    `user_id`    INT                   NOT NULL COMMENT '사용자 ID',
    `med_date`   DATE                  NOT NULL COMMENT '복약 날짜',
    `time_slot`  ENUM ('아침','점심','저녁') NOT NULL COMMENT '시간대',
    `drug_name`  VARCHAR(100)          NOT NULL COMMENT '약 이름',
    `photo_url`  VARCHAR(255)          NULL COMMENT '약봉투 사진 URL',
    `created_at` DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`user_id`, `med_date`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 9. 복약 알림 설정 테이블
CREATE TABLE `medication_reminders`
(
    `id`            INT                    NOT NULL AUTO_INCREMENT,
    `medication_id` INT                    NOT NULL COMMENT 'medications.id',
    `reminder_type` ENUM ('식전','식후','취침전') NOT NULL COMMENT '알림 타입',
    `is_enabled`    TINYINT(1)             NOT NULL DEFAULT 1 COMMENT '알림 사용 여부',
    `created_at`    DATETIME               NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`medication_id`),
    FOREIGN KEY (`medication_id`) REFERENCES `medications` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 10. 건강 Tip 테이블
CREATE TABLE `tips`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `content`    VARCHAR(500) NOT NULL COMMENT '건강 팁 내용',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 11. 질병-팁 연결 테이블 (다중 질병 관련 팁 지원)
CREATE TABLE `disease_tips`
(
    `disease_id` INT NOT NULL,
    `tip_id`     INT NOT NULL,
    PRIMARY KEY (`disease_id`, `tip_id`),
    FOREIGN KEY (`disease_id`) REFERENCES `diseases` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tip_id`) REFERENCES `tips` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 12. 사용자-팁 노출 이력 테이블
CREATE TABLE `user_tips`
(
    `user_id`  INT      NOT NULL,
    `tip_id`   INT      NOT NULL,
    `shown_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '노출 시각',
    PRIMARY KEY (`user_id`, `tip_id`, `shown_at`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tip_id`) REFERENCES `tips` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 13. 주간 건강 리포트 테이블
CREATE TABLE `weekly_reports`
(
    `id`            INT           NOT NULL AUTO_INCREMENT,
    `user_id`       INT           NOT NULL COMMENT '사용자 ID',
    `start_date`    DATE          NOT NULL COMMENT '시작 날짜',
    `end_date`      DATE          NOT NULL COMMENT '종료 날짜',
    `meal_count`    INT           NOT NULL DEFAULT 0 COMMENT '식사 횟수',
    `med_adherence` DECIMAL(5, 2) NOT NULL DEFAULT 0.00 COMMENT '복약 준수율(%)',
    `health_score`  INT           NULL COMMENT '건강 점수',
    `created_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX (`user_id`, `start_date`, `end_date`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 14. 식단 추천 테이블
CREATE TABLE `diet_recommendations`
(
    `id`         INT      NOT NULL AUTO_INCREMENT,
    `report_id`  INT      NOT NULL COMMENT '주간 리포트 ID',
    `content`    TEXT     NOT NULL COMMENT '추천 내용',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`report_id`) REFERENCES `weekly_reports` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 15. 세션 관리 테이블 (JWT 토큰)
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
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 16. 질병 마스터 데이터 삽입
INSERT INTO `diseases` (`name`, `description`)
VALUES ('없음', '해당 없음'),
       ('당뇨', '혈당 조절에 문제가 있는 질환'),
       ('고혈압', '혈압이 정상 범위보다 높은 상태'),
       ('신장질환', '신장 기능에 문제가 있는 질환'),
       ('심장질환', '심장 기능에 문제가 있는 질환'),
       ('관절염', '관절에 염증이 생기는 질환'),
       ('기타', '기타 질환');

SELECT *
FROM users;
SELECT *
FROM meals;

-- 테스트 사용자 추가
INSERT INTO users (login_id,
                   password_hash,
                   name,
                   birth_date,
                   gender,
                   email,
                   height,
                   weight,
                   theme_pref,
                   diabetes,
                   hypertension,
                   hyperlipidemia,
                   heart_disease,
                   kidney_disease,
                   allergies)
VALUES ('test1',
        '$2a$10$IrTU5lGx8ZLd7Ht.NMDmxeQY8YVA2ufJ/Z5yjULBF5oD0OPMvsb7O', -- 'password123$'의 해시
        '홍길동',
        '1990-01-01',
        'M',
        'test1@example.com',
        175.0,
        70.0,
        'default',
        1, -- 당뇨 있음
        0, -- 고혈압 없음
        0, -- 고지혈증 없음
        0, -- 심장질환 없음
        0, -- 신장질환 없음
        0 -- 알레르기 없음
       );


INSERT INTO meals (user_id,
                   eat_date,
                   time_slot,
                   food_name,
                   memo)
VALUES (1, -- 방금 추가한 test1 사용자의 ID
        CURRENT_DATE(),
        '아침',
        '현미밥과 된장국',
        '건강한 한식 아침');

# 음식 영양소 구성 테이블 생성
CREATE TABLE food_nutrients
(
    id            INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    food_name     VARCHAR(200) NOT NULL COMMENT '식품명',
    serving_size  VARCHAR(100) COMMENT '영양성분함량기준량',
    energy        FLOAT COMMENT '에너지(kcal)',
    protein       FLOAT COMMENT '단백질(g)',
    fat           FLOAT COMMENT '지방(g)',
    carbohydrate  FLOAT COMMENT '탄수화물(g)',
    sugar         FLOAT COMMENT '당류(g)',
    fiber         FLOAT COMMENT '식이섬유(g)',
    potassium     FLOAT COMMENT '칼륨(mg)',
    sodium        FLOAT COMMENT '나트륨(mg)',
    cholesterol   FLOAT COMMENT '콜레스테롤(mg)',
    saturated_fat FLOAT COMMENT '포화지방산(g)'
);

select *
from food_nutrients;

select *
from diseases;

select * from disease_nutrient_guidelines;

-- 질병별 영양소 가이드라인 테이블
CREATE TABLE disease_nutrient_guidelines
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    disease_id     INT NOT NULL,
    nutrient_id    INT NOT NULL,
    min_value      DECIMAL(10, 2),
    max_value      DECIMAL(10, 2),
    importance     INT COMMENT '1-5 중요도, 높을수록 중요',
    recommendation TEXT,
    is_restriction BOOLEAN   DEFAULT FALSE COMMENT 'TRUE: 제한해야 함, FALSE: 권장함',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (disease_id) REFERENCES diseases (id),
    FOREIGN KEY (nutrient_id) REFERENCES food_nutrients (id)
);

-- 고혈압 영양소 가이드라인
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, importance, is_restriction, recommendation)
VALUES (3, 1, 0, 2000, 5, TRUE, '나트륨 섭취를 2000mg(소금 5g) 이하로 제한하세요'),
       (3, 2, 3500, 4700, 4, FALSE, '칼륨이 풍부한 식품을 섭취하세요'),
       (3, 5, 0, 50, 3, TRUE, '지방 섭취를 제한하세요'),
       (3, 6, 0, 15, 4, TRUE, '포화지방 섭취를 제한하세요'),
       (3, 8, 25, 30, 3, FALSE, '식이섬유를 충분히 섭취하세요');

-- 당뇨병 영양소 가이드라인
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, importance, is_restriction, recommendation)
VALUES (2, 3, 45, 60, 5, TRUE, '총 칼로리의 45-60%를 탄수화물로 섭취하세요'),
       (2, 9, 0, 25, 5, TRUE, '첨가당 섭취를 25g 이하로 제한하세요'),
       (2, 8, 25, 30, 4, FALSE, '식이섬유 섭취를 늘리세요'),
       (2, 4, 15, 20, 3, FALSE, '적절한 단백질을 섭취하세요'),
       (2, 6, 0, 15, 4, TRUE, '포화지방 섭취를 제한하세요');

-- 심장질환 영양소 가이드라인
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, importance, is_restriction, recommendation)
VALUES (5, 1, 0, 2000, 5, TRUE, '나트륨 섭취를 2000mg 이하로 제한하세요'),
       (5, 7, 0, 200, 5, TRUE, '콜레스테롤 섭취를 200mg 이하로 제한하세요'),
       (5, 6, 0, 15, 5, TRUE, '포화지방 섭취를 제한하세요'),
       (5, 8, 25, 30, 4, FALSE, '식이섬유를 충분히 섭취하세요'),
       (5, 5, 20, 35, 3, TRUE, '총 지방 섭취를 제한하세요');

-- 신장질환 영양소 가이드라인
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, importance, is_restriction, recommendation)
VALUES (4, 1, 0, 2000, 5, TRUE, '나트륨 섭취를 2000mg 이하로 제한하세요'),
       (4, 4, 0.6, 0.8, 5, TRUE, '단백질 섭취를 체중 kg당 0.6-0.8g으로 제한하세요'),
       (4, 10, 0, 800, 4, TRUE, '인 섭취를 제한하세요'),
       (4, 2, 0, 2000, 4, TRUE, '신장 기능에 따라 칼륨 섭취를 제한할 수 있습니다'),
       (4, 3, 50, 60, 3, FALSE, '충분한 열량을 섭취하세요');

-- 간질환 영양소 가이드라인
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, importance, is_restriction, recommendation)
VALUES (5, 11, 0, 20, 5, TRUE, '금주가 권장되며, 불가피한 경우 여성 20g, 남성 40g 이하로 제한하세요'),
       (5, 4, 60, 80, 4, TRUE, '간성뇌증 위험이 있는 경우 단백질을 제한하세요'),
       (5, 1, 0, 2000, 3, TRUE, '복수가 있는 경우 나트륨을 제한하세요'),
       (5, 3, 0, 0, 4, FALSE, '충분한 열량을 섭취하세요'),
       (5, 5, 0, 30, 3, TRUE, '지방 섭취를 적절히 제한하세요');