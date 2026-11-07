-- ERP 업무 테이블, 제약조건, 인덱스 생성
CREATE SEQUENCE erp_item_code_seq START WITH 1 MAXVALUE 999999 NOCYCLE;

CREATE SEQUENCE erp_clinic_code_seq START WITH 1 MAXVALUE 999999 NOCYCLE;

-- ERP 사용자 계정과 로그인 정보
CREATE TABLE erp_user (
    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    is_active CHAR(1) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'Y',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CHECK (
        CHAR_LENGTH(TRIM(login_id)) BETWEEN 1
        AND 50
    ),
    CHECK (
        CHAR_LENGTH(TRIM(display_name)) BETWEEN 1
        AND 100
    ),
    CHECK (is_active IN ('Y', 'N'))
) ENGINE = InnoDB;

-- 품목 코드, 포장 규격, 단위 등 품목 기준정보
CREATE TABLE erp_item (
    item_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(20) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
    item_name VARCHAR(100) NOT NULL,
    package_spec VARCHAR(100) NOT NULL,
    unit_name VARCHAR(10) NOT NULL,
    is_active CHAR(1) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'Y',
    ever_used CHAR(1) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_by BIGINT NOT NULL,
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (created_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (updated_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CHECK (item_code REGEXP '^HR-[0-9]{6}$'),
    CHECK (
        CHAR_LENGTH(TRIM(item_name)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(package_spec)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(unit_name)) BETWEEN 1
        AND 10
    ),
    CHECK (is_active IN ('Y', 'N')),
    CHECK (ever_used IN ('Y', 'N')),
    CHECK (updated_at >= created_at),
    INDEX ix_item_active (is_active, item_id)
) ENGINE = InnoDB;

-- 한의원 코드, 연락처, 납품 주소 등 거래처 기준정보
CREATE TABLE erp_clinic (
    clinic_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    clinic_code VARCHAR(20) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
    clinic_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    delivery_address VARCHAR(200) NOT NULL,
    is_active CHAR(1) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'Y',
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_by BIGINT NOT NULL,
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (created_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (updated_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CHECK (clinic_code REGEXP '^CL-[0-9]{6}$'),
    CHECK (
        CHAR_LENGTH(TRIM(clinic_name)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(phone)) BETWEEN 1
        AND 30
    ),
    CHECK (phone REGEXP '^[0-9 ()+-]+$'),
    CHECK (
        CHAR_LENGTH(TRIM(delivery_address)) BETWEEN 1
        AND 200
    ),
    CHECK (is_active IN ('Y', 'N')),
    CHECK (updated_at >= created_at),
    INDEX ix_clinic_active (is_active, clinic_id)
) ENGINE = InnoDB;

-- 품목별 현재고와 마지막 재고 반영 순번
CREATE TABLE erp_stock (
    item_id BIGINT NOT NULL PRIMARY KEY,
    quantity INT NOT NULL DEFAULT 0,
    movement_seq BIGINT NOT NULL DEFAULT 0,
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (item_id) REFERENCES erp_item(item_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CHECK (
        quantity BETWEEN 0
        AND 999999999
    ),
    CHECK (movement_seq >= 0)
) ENGINE = InnoDB;

-- 입고·주문 등록의 중복 처리를 방지하는 요청 기록
CREATE TABLE erp_request (
    request_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request_key CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
    operation_type VARCHAR(20) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    requested_by BIGINT NOT NULL,
    payload_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    completed_at DATETIME(6),
    FOREIGN KEY (requested_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CHECK (
        operation_type IN ('RECEIPT_CREATE', 'ORDER_CREATE')
    ),
    CHECK (
        completed_at IS NULL
        OR completed_at >= created_at
    )
) ENGINE = InnoDB;

-- 입고 문서와 입고 당시 품목 정보
CREATE TABLE erp_receipt (
    receipt_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE,
    item_id BIGINT NOT NULL,
    item_name_snapshot VARCHAR(100) NOT NULL,
    package_spec_snapshot VARCHAR(100) NOT NULL,
    unit_name_snapshot VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (request_id) REFERENCES erp_request(request_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (item_id) REFERENCES erp_item(item_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CHECK (
        quantity BETWEEN 1
        AND 999999
    ),
    CHECK (
        CHAR_LENGTH(TRIM(item_name_snapshot)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(package_spec_snapshot)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(unit_name_snapshot)) BETWEEN 1
        AND 10
    ),
    INDEX ix_receipt_date (created_at, receipt_id),
    INDEX ix_receipt_item_date (item_id, created_at, receipt_id)
) ENGINE = InnoDB;

-- 한의원 주문 문서와 취소·출고·납품 상태
CREATE TABLE erp_order (
    order_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE,
    clinic_id BIGINT NOT NULL,
    clinic_name_snapshot VARCHAR(100) NOT NULL,
    phone_snapshot VARCHAR(30) NOT NULL,
    delivery_address_snapshot VARCHAR(200) NOT NULL,
    status VARCHAR(20) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'RECEIVED',
    version BIGINT NOT NULL DEFAULT 0,
    total_amount BIGINT NOT NULL,
    cancel_reason VARCHAR(200),
    cancelled_by BIGINT,
    cancelled_at DATETIME(6),
    shipped_by BIGINT,
    shipped_at DATETIME(6),
    delivered_by BIGINT,
    delivered_at DATETIME(6),
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_by BIGINT NOT NULL,
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (request_id) REFERENCES erp_request(request_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (clinic_id) REFERENCES erp_clinic(clinic_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (cancelled_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (shipped_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (delivered_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (updated_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CHECK (version >= 0),
    CHECK (
        total_amount BETWEEN 1
        AND 499999450000050
    ),
    CHECK (
        CHAR_LENGTH(TRIM(clinic_name_snapshot)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(phone_snapshot)) BETWEEN 1
        AND 30
    ),
    CHECK (
        CHAR_LENGTH(TRIM(delivery_address_snapshot)) BETWEEN 1
        AND 200
    ),
    CHECK (
        (
            status = 'RECEIVED'
            AND cancel_reason IS NULL
            AND cancelled_by IS NULL
            AND cancelled_at IS NULL
            AND shipped_by IS NULL
            AND shipped_at IS NULL
            AND delivered_by IS NULL
            AND delivered_at IS NULL
        )
        OR (
            status = 'CANCELLED'
            AND cancel_reason IS NOT NULL
            AND CHAR_LENGTH(TRIM(cancel_reason)) BETWEEN 1
            AND 200
            AND cancelled_by IS NOT NULL
            AND cancelled_at IS NOT NULL
            AND shipped_by IS NULL
            AND shipped_at IS NULL
            AND delivered_by IS NULL
            AND delivered_at IS NULL
        )
        OR (
            status = 'SHIPPED'
            AND cancel_reason IS NULL
            AND cancelled_by IS NULL
            AND cancelled_at IS NULL
            AND shipped_by IS NOT NULL
            AND shipped_at IS NOT NULL
            AND delivered_by IS NULL
            AND delivered_at IS NULL
        )
        OR (
            status = 'DELIVERED'
            AND cancel_reason IS NULL
            AND cancelled_by IS NULL
            AND cancelled_at IS NULL
            AND shipped_by IS NOT NULL
            AND shipped_at IS NOT NULL
            AND delivered_by IS NOT NULL
            AND delivered_at IS NOT NULL
        )
    ),
    CHECK (updated_at >= created_at),
    CHECK (
        shipped_at IS NULL
        OR shipped_at >= created_at
    ),
    CHECK (
        delivered_at IS NULL
        OR delivered_at >= shipped_at
    ),
    CHECK (
        cancelled_at IS NULL
        OR cancelled_at >= created_at
    ),
    INDEX ix_order_date (created_at, order_id),
    INDEX ix_order_status_date (status, created_at, order_id),
    INDEX ix_order_clinic_date (clinic_id, created_at, order_id)
) ENGINE = InnoDB;

-- 주문별 품목, 수량, 단가 및 금액 상세
CREATE TABLE erp_order_line (
    order_line_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    line_no SMALLINT NOT NULL,
    item_id BIGINT NOT NULL,
    item_name_snapshot VARCHAR(100) NOT NULL,
    package_spec_snapshot VARCHAR(100) NOT NULL,
    unit_name_snapshot VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    unit_price INT NOT NULL,
    line_amount BIGINT GENERATED ALWAYS AS (
        CAST(quantity AS SIGNED) * CAST(unit_price AS SIGNED)
    ) STORED,
    FOREIGN KEY (order_id) REFERENCES erp_order(order_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (item_id) REFERENCES erp_item(item_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CHECK (
        line_no BETWEEN 1
        AND 50
    ),
    CHECK (
        quantity BETWEEN 1
        AND 999999
    ),
    CHECK (
        unit_price BETWEEN 1
        AND 9999999
    ),
    CHECK (
        CHAR_LENGTH(TRIM(item_name_snapshot)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(package_spec_snapshot)) BETWEEN 1
        AND 100
    ),
    CHECK (
        CHAR_LENGTH(TRIM(unit_name_snapshot)) BETWEEN 1
        AND 10
    ),
    UNIQUE (order_id, item_id),
    UNIQUE (order_id, line_no),
    INDEX ix_order_line_item (item_id)
) ENGINE = InnoDB;

-- 품목별 입출고 증감 수량과 반영 직후 재고 이력
CREATE TABLE erp_stock_movement (
    movement_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    movement_seq BIGINT NOT NULL,
    movement_type VARCHAR(3) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    receipt_id BIGINT UNIQUE,
    order_line_id BIGINT UNIQUE,
    quantity_delta INT NOT NULL,
    balance_after INT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (item_id) REFERENCES erp_item(item_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (receipt_id) REFERENCES erp_receipt(receipt_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (order_line_id) REFERENCES erp_order_line(order_line_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (created_by) REFERENCES erp_user(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    UNIQUE (item_id, movement_seq),
    CHECK (movement_seq >= 1),
    CHECK (
        balance_after BETWEEN 0
        AND 999999999
    ),
    CHECK (
        (
            movement_type = 'IN'
            AND receipt_id IS NOT NULL
            AND order_line_id IS NULL
            AND quantity_delta BETWEEN 1
            AND 999999
        )
        OR (
            movement_type = 'OUT'
            AND receipt_id IS NULL
            AND order_line_id IS NOT NULL
            AND quantity_delta BETWEEN -999999
            AND -1
        )
    ),
    INDEX ix_movement_date (created_at, movement_id),
    INDEX ix_movement_item_date (item_id, created_at, movement_id),
    INDEX ix_movement_type_date (movement_type, created_at, movement_id)
) ENGINE = InnoDB;
