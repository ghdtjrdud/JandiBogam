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
    `diabetes`       TINYINT(1)                         DEFAULT 0, # 당뇨
    `hypertension`   TINYINT(1)                         DEFAULT 0, # 고혈압
    `heart_disease`  TINYINT(1)                         DEFAULT 0, # 심장질환
    `kidney_disease` TINYINT(1)                         DEFAULT 0, # 신장질환
    `liver_disease`  TINYINT(1)                         DEFAULT 0, # 간질환
    `family_code`    VARCHAR(50)               NULL,
    `created_at`     DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

select * from  users;

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

select * from user_diseases;

-- 4. 가족 그룹 테이블
CREATE TABLE `groups`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(100) NOT NULL COMMENT '그룹 이름',
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
    `time_slot`  ENUM ('아침','점심','저녁') NOT NULL COMMENT '시간대',
    `photo_url`  VARCHAR(255)               NULL COMMENT '음식 사진 URL',
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
    `drug_name`  VARCHAR(100)          NOT NULL COMMENT '약 이름',
    `drug_type`  VARCHAR(100)          NOT NULL COMMENT '약 종류',
    `med_date`   DATE                  NOT NULL COMMENT '복약 날짜',
    `time_slot`  VARCHAR(100)          NOT NULL COMMENT '시간대',
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
    `id`                INT           NOT NULL AUTO_INCREMENT,
    `user_id`           INT           NOT NULL COMMENT '사용자 ID',
    `start_date`        DATE          NOT NULL COMMENT '시작 날짜',
    `end_date`          DATE          NOT NULL COMMENT '종료 날짜',
    `meal_count`        INT           NOT NULL DEFAULT 0 COMMENT '식사 횟수',
    `health_score`      INT           NULL COMMENT '건강 점수',
    `ai_recommendation` TEXT          COMMENT 'AI 식단 추천 내용',
    `ai_generated_at`   DATETIME      NULL COMMENT 'AI 추천 생성 시간',
    `health_tips`       DATETIME      COMMENT 'AI 개인화 건강 조언',
    `created_at`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
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


-- 16. 음식 영양소 구성 테이블 생성
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


-- 17. 한 식단에 여러가지 음식이 들어가는 테이블
CREATE TABLE meal_foods (
                            meal_id INT NOT NULL COMMENT 'meals.id',
                            food_id INT NOT NULL COMMENT 'food_nutrients.id',
                            PRIMARY KEY (meal_id, food_id),
                            FOREIGN KEY (meal_id)
                                REFERENCES meals(id),
                            FOREIGN KEY (food_id)
                                REFERENCES food_nutrients(id)
);

-- 18. 영양소 마스터 테이블 생성
CREATE TABLE nutrients (
                           id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL UNIQUE COMMENT '영양소명',
                           unit VARCHAR(20) NOT NULL COMMENT '단위(예: g, mg)',
                           description VARCHAR(255) NULL COMMENT '설명',
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 18-1. 기본 영양소 데이터 삽입
INSERT INTO nutrients (name, unit, description)
VALUES
    ('나트륨', 'mg', '소금의 주요 성분으로 고혈압과 관련'),
    ('칼륨', 'mg', '나트륨 배출을 돕고 혈압 조절에 도움'),
    ('탄수화물', 'g', '주요 에너지원'),
    ('단백질', 'g', '근육과 조직 형성에 필요'),
    ('지방', 'g', '에너지 저장 및 호르몬 생성에 관여'),
    ('포화지방', 'g', '동물성 식품에 많이 포함된 지방'),
    ('콜레스테롤', 'mg', '세포막 구성 및 호르몬 생성에 관여'),
    ('식이섬유', 'g', '소화를 돕고 혈당 조절에 도움'),
    ('당류', 'g', '단순 탄수화물로 혈당 상승을 유발'),
    ('에너지', 'kcal', '음식에서 얻는 열량');

-- 19. 질병별 영양소 가이드라인 테이블
CREATE TABLE disease_nutrient_guidelines
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    disease_id     INT NOT NULL,
    nutrient_id    INT NOT NULL,
    min_value      DECIMAL(10, 2),
    max_value      DECIMAL(10, 2),
    is_restriction BOOLEAN   DEFAULT FALSE COMMENT 'TRUE: 제한해야 함, FALSE: 권장함',
    recommendation TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (disease_id) REFERENCES diseases (id),
    FOREIGN KEY (nutrient_id) REFERENCES nutrients (id)
);

-- 20. 일일 식단 기록에 따른 영양소 요약 테이블
CREATE TABLE daily_nutrient_summary (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '사용자 ID',
    summary_date DATE NOT NULL COMMENT '집계 날짜',
    nutrient_id INT NOT NULL COMMENT '영양소 ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '일일 섭취량 합계',
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY (user_id, summary_date, nutrient_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (nutrient_id) REFERENCES nutrients(id) ON DELETE CASCADE
);




-- 질병 마스터 데이터 삽입
INSERT INTO `diseases` (`name`, `description`)
VALUES ('없음', '해당 없음'),
       ('당뇨', '혈당 조절에 문제가 있는 질환'),
       ('고혈압', '혈압이 정상 범위보다 높은 상태'),
       ('신장질환', '신장 기능에 문제가 있는 질환'),
       ('심장질환', '심장 기능에 문제가 있는 질환'),
       ('간질환', '간 기능에 문제가 있는 질환'),
       ('기타', '기타 질환');


-- 일반인을 위한 영양소 가이드라인 (기본 10가지 영양소)
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, is_restriction, recommendation)
VALUES
-- 에너지(칼로리)
(1, (SELECT id FROM nutrients WHERE name = '에너지'), 2000, 2500, FALSE, '성인 여성은 하루 약 2000kcal, 성인 남성은 약 2500kcal 섭취가 권장됩니다.'),
-- 나트륨
(1, (SELECT id FROM nutrients WHERE name = '나트륨'), 1500, 2300, TRUE, '나트륨 섭취를 2300mg 이하로 유지하세요. 저염식이 권장됩니다.'),
-- 칼륨
(1, (SELECT id FROM nutrients WHERE name = '칼륨'), 3500, 4700, FALSE, '칼륨은 하루 3500-4700mg 섭취가 권장됩니다.'),
-- 탄수화물
(1, (SELECT id FROM nutrients WHERE name = '탄수화물'), 55, 65, FALSE, '총 칼로리의 55-65%를 탄수화물로 섭취하는 것이 좋습니다.'),
-- 단백질
(1, (SELECT id FROM nutrients WHERE name = '단백질'), 0.8, 1.5, FALSE, '체중 kg당 0.8-1.5g의 단백질 섭취가 권장됩니다.'),
-- 지방
(1, (SELECT id FROM nutrients WHERE name = '지방'), 15, 30, TRUE, '총 칼로리의 15-30%를 지방으로 섭취하세요.'),
-- 포화지방
(1, (SELECT id FROM nutrients WHERE name = '포화지방'), 0, 7, TRUE, '포화지방은 총 칼로리의 7% 이하로 제한하세요.'),
-- 콜레스테롤
(1, (SELECT id FROM nutrients WHERE name = '콜레스테롤'), 0, 300, TRUE, '콜레스테롤은 하루 300mg 이하로 제한하세요.'),
-- 식이섬유
(1, (SELECT id FROM nutrients WHERE name = '식이섬유'), 20, 25, FALSE, '식이섬유는 남성 25g, 여성 20g 이상 섭취가 권장됩니다.'),
-- 당류
(1, (SELECT id FROM nutrients WHERE name = '당류'), 0, 50, TRUE, '첨가당은 총 칼로리의 10% 이하(약 50g)로 제한하세요.');

-- 고혈압 영양소 가이드라인 (10가지 모두 포함)
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, is_restriction, recommendation)
VALUES
-- 특화된 가이드라인 (5개)
(3, (SELECT id FROM nutrients WHERE name = '나트륨'), 0, 2000, TRUE, '나트륨 섭취를 2000mg(소금 5g) 이하로 제한하세요'),
(3, (SELECT id FROM nutrients WHERE name = '칼륨'), 3500, 4700, FALSE, '칼륨이 풍부한 식품을 섭취하세요'),
(3, (SELECT id FROM nutrients WHERE name = '지방'), 0, 50, TRUE, '지방 섭취를 제한하세요'),
(3, (SELECT id FROM nutrients WHERE name = '포화지방'), 0, 15, TRUE, '포화지방 섭취를 제한하세요'),
(3, (SELECT id FROM nutrients WHERE name = '식이섬유'), 25, 30, FALSE, '식이섬유를 충분히 섭취하세요'),
-- 일반 가이드라인 사용 (5개)
(3, (SELECT id FROM nutrients WHERE name = '에너지'), 2000, 2500, FALSE, '일반적인 에너지 섭취량을 유지하되, 체중 관리에 신경쓰세요'),
(3, (SELECT id FROM nutrients WHERE name = '탄수화물'), 55, 65, FALSE, '총 칼로리의 55-65%를 탄수화물로 섭취하세요'),
(3, (SELECT id FROM nutrients WHERE name = '단백질'), 0.8, 1.5, FALSE, '적정 단백질 섭취는 고혈압 관리에 도움이 됩니다'),
(3, (SELECT id FROM nutrients WHERE name = '콜레스테롤'), 0, 300, TRUE, '콜레스테롤은 하루 300mg 이하로 제한하세요'),
(3, (SELECT id FROM nutrients WHERE name = '당류'), 0, 50, TRUE, '첨가당 섭취를 제한하여 체중 관리와 고혈압 예방에 도움이 됩니다');

-- 당뇨병 영양소 가이드라인 (10가지 모두 포함)
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, is_restriction, recommendation)
VALUES
-- 특화된 가이드라인 (5개)
(2, (SELECT id FROM nutrients WHERE name = '탄수화물'), 45, 60, TRUE, '총 칼로리의 45-60%를 탄수화물로 섭취하세요'),
(2, (SELECT id FROM nutrients WHERE name = '당류'), 0, 25, TRUE, '첨가당 섭취를 25g 이하로 제한하세요'),
(2, (SELECT id FROM nutrients WHERE name = '식이섬유'), 25, 30, FALSE, '식이섬유 섭취를 늘리세요'),
(2, (SELECT id FROM nutrients WHERE name = '단백질'), 15, 20, FALSE, '적절한 단백질을 섭취하세요'),
(2, (SELECT id FROM nutrients WHERE name = '포화지방'), 0, 15, TRUE, '포화지방 섭취를 제한하세요'),
-- 일반 가이드라인 사용 (5개)
(2, (SELECT id FROM nutrients WHERE name = '에너지'), 2000, 2500, FALSE, '개인 체중에 맞는 적절한 열량을 섭취하되, 혈당 관리를 위해 체중 유지를 목표로 하세요'),
(2, (SELECT id FROM nutrients WHERE name = '나트륨'), 1500, 2300, TRUE, '나트륨 섭취를 2300mg 이하로 유지하세요'),
(2, (SELECT id FROM nutrients WHERE name = '칼륨'), 3500, 4700, FALSE, '칼륨이 풍부한 식품을 섭취하세요'),
(2, (SELECT id FROM nutrients WHERE name = '지방'), 15, 30, TRUE, '총 칼로리의 15-30%를 지방으로 섭취하되, 불포화지방 위주로 섭취하세요'),
(2, (SELECT id FROM nutrients WHERE name = '콜레스테롤'), 0, 300, TRUE, '콜레스테롤 섭취는 300mg 이하로 제한하세요');

-- 심장질환 영양소 가이드라인 (10가지 모두 포함)
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, is_restriction, recommendation)
VALUES
-- 특화된 가이드라인 (5개)
(5, (SELECT id FROM nutrients WHERE name = '나트륨'), 0, 2000, TRUE, '나트륨 섭취를 2000mg 이하로 제한하세요'),
(5, (SELECT id FROM nutrients WHERE name = '콜레스테롤'), 0, 200, TRUE, '콜레스테롤 섭취를 200mg 이하로 제한하세요'),
(5, (SELECT id FROM nutrients WHERE name = '포화지방'), 0, 15, TRUE, '포화지방 섭취를 제한하세요'),
(5, (SELECT id FROM nutrients WHERE name = '식이섬유'), 25, 30, FALSE, '식이섬유를 충분히 섭취하세요'),
(5, (SELECT id FROM nutrients WHERE name = '지방'), 20, 35, TRUE, '총 지방 섭취를 제한하세요'),
-- 일반 가이드라인 사용 (5개)
(5, (SELECT id FROM nutrients WHERE name = '에너지'), 1800, 2200, TRUE, '적절한 체중을 유지하는 것이 심장 부담을 줄이는 데 도움이 됩니다'),
(5, (SELECT id FROM nutrients WHERE name = '칼륨'), 3500, 4700, FALSE, '칼륨이 풍부한 식품을 섭취하세요'),
(5, (SELECT id FROM nutrients WHERE name = '탄수화물'), 55, 65, FALSE, '정제된 탄수화물보다 통곡물 위주로 섭취하세요'),
(5, (SELECT id FROM nutrients WHERE name = '단백질'), 0.8, 1.5, FALSE, '식물성 단백질과 생선 단백질을 우선적으로 선택하세요'),
(5, (SELECT id FROM nutrients WHERE name = '당류'), 0, 25, TRUE, '첨가당 섭취를 25g 이하로 제한하세요');

-- 신장질환 영양소 가이드라인 (10가지 모두 포함)
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, is_restriction, recommendation)
VALUES
-- 특화된 가이드라인 (5개)
(4, (SELECT id FROM nutrients WHERE name = '나트륨'), 0, 2000, TRUE, '나트륨 섭취를 2000mg 이하로 제한하세요'),
(4, (SELECT id FROM nutrients WHERE name = '단백질'), 0.6, 0.8, TRUE, '단백질 섭취를 체중 kg당 0.6-0.8g으로 제한하세요'),
(4, (SELECT id FROM nutrients WHERE name = '칼륨'), 0, 2000, TRUE, '신장 기능에 따라 칼륨 섭취를 제한할 수 있습니다'),
(4, (SELECT id FROM nutrients WHERE name = '에너지'), 30, 35, FALSE, '체중 kg당 30-35kcal의 충분한 열량을 섭취하세요'),
(4, (SELECT id FROM nutrients WHERE name = '식이섬유'), 20, 25, FALSE, '식이섬유는 변비 예방에 도움이 되지만 과다 섭취를 피하세요'),
-- 일반 가이드라인 사용 (5개)
(4, (SELECT id FROM nutrients WHERE name = '탄수화물'), 50, 60, FALSE, '충분한 에너지 섭취를 위해 탄수화물 비율을 적절히 유지하세요'),
(4, (SELECT id FROM nutrients WHERE name = '지방'), 25, 35, FALSE, '건강한 지방을 적절히 섭취하여 열량을 보충하세요'),
(4, (SELECT id FROM nutrients WHERE name = '포화지방'), 0, 7, TRUE, '포화지방은 총 칼로리의 7% 이하로 제한하세요'),
(4, (SELECT id FROM nutrients WHERE name = '콜레스테롤'), 0, 300, TRUE, '콜레스테롤은 하루 300mg 이하로 제한하세요'),
(4, (SELECT id FROM nutrients WHERE name = '당류'), 0, 30, TRUE, '단순당 섭취를 제한하고 복합탄수화물을 선택하세요');

-- 간질환 영양소 가이드라인 (10가지 모두 포함)
INSERT INTO disease_nutrient_guidelines
(disease_id, nutrient_id, min_value, max_value, is_restriction, recommendation)
VALUES
-- 특화된 가이드라인 (5개)
(6, (SELECT id FROM nutrients WHERE name = '에너지'), 30, 40, FALSE, '체중 kg당 30-40kcal의 충분한 열량을 섭취하세요'),
(6, (SELECT id FROM nutrients WHERE name = '단백질'), 60, 80, TRUE, '간성뇌증 위험이 있는 경우 단백질을 제한하세요'),
(6, (SELECT id FROM nutrients WHERE name = '나트륨'), 0, 2000, TRUE, '복수가 있는 경우 나트륨을 제한하세요'),
(6, (SELECT id FROM nutrients WHERE name = '탄수화물'), 50, 65, FALSE, '총 칼로리의 50-65%를 탄수화물로 섭취하세요'),
(6, (SELECT id FROM nutrients WHERE name = '지방'), 0, 30, TRUE, '지방 섭취를 적절히 제한하세요'),
-- 일반 가이드라인 사용 (5개)
(6, (SELECT id FROM nutrients WHERE name = '칼륨'), 3500, 4700, FALSE, '균형 잡힌 식단을 통해 충분한 칼륨을 섭취하세요'),
(6, (SELECT id FROM nutrients WHERE name = '포화지방'), 0, 7, TRUE, '포화지방은 총 칼로리의 7% 이하로 제한하세요'),
(6, (SELECT id FROM nutrients WHERE name = '콜레스테롤'), 0, 300, TRUE, '콜레스테롤은 하루 300mg 이하로 제한하세요'),
(6, (SELECT id FROM nutrients WHERE name = '식이섬유'), 20, 25, FALSE, '식이섬유는 소화를 돕고 독소 배출에 도움이 됩니다'),
(6, (SELECT id FROM nutrients WHERE name = '당류'), 0, 30, TRUE, '과도한 당류 섭취는 간에 부담을 줄 수 있으므로 제한하세요');




select * from users;
