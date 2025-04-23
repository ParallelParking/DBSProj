CREATE OR REPLACE FUNCTION check_booking_not_null_fields()
RETURNS TRIGGER AS $$
BEGIN
  -- Check essential fields derived from the composite primary key and relationships
  IF NEW.block IS NULL THEN
    RAISE EXCEPTION 'Booking cannot be inserted: "block" cannot be NULL.';
  END IF;
  IF NEW.room_no IS NULL THEN
    RAISE EXCEPTION 'Booking cannot be inserted: "room_no" cannot be NULL.';
  END IF;
  IF NEW.start_time IS NULL THEN
    RAISE EXCEPTION 'Booking cannot be inserted: "start_time" cannot be NULL.';
  END IF;

  -- Check other logically required fields for a valid initial booking request
  IF NEW.end_time IS NULL THEN
    RAISE EXCEPTION 'Booking cannot be inserted: "end_time" cannot be NULL.';
  END IF;
  IF NEW.purpose IS NULL THEN
    RAISE EXCEPTION 'Booking cannot be inserted: "purpose" cannot be NULL.';
  END IF;
  IF NEW.student_email IS NULL THEN
    RAISE EXCEPTION 'Booking cannot be inserted: "student_email" cannot be NULL.';
  END IF;
  IF NEW.club_name IS NULL THEN
    RAISE EXCEPTION 'Booking cannot be inserted: "club_name" cannot be NULL';
  END IF;
  -- Note: overall_status is usually set by the application logic/default, not checked on initial insert.

  -- If all checks pass, allow the insertion
  RETURN NEW;
END;
$$ LANGUAGE plpgsql; //

DROP TRIGGER IF EXISTS trg_check_booking_not_null ON booking; //

CREATE OR REPLACE FUNCTION check_approval_not_null_fields()
RETURNS TRIGGER AS $$
BEGIN
  -- Check Foreign Key components referencing the booking
  IF NEW.block IS NULL THEN
    RAISE EXCEPTION 'Booking Approval cannot be inserted: "block" cannot be NULL.'; 
  END IF;
  IF NEW.room_no IS NULL THEN
    RAISE EXCEPTION 'Booking Approval cannot be inserted: "room_no" cannot be NULL.'; 
  END IF;
  IF NEW.start_time IS NULL THEN
    RAISE EXCEPTION 'Booking Approval cannot be inserted: "start_time" cannot be NULL.'; 
  END IF;

  -- Check essential approval details
  IF NEW.approver_email IS NULL THEN
    RAISE EXCEPTION 'Booking Approval cannot be inserted: "approver_email" cannot be NULL.'; 
  END IF;
  IF NEW.approver_role IS NULL THEN
    RAISE EXCEPTION 'Booking Approval cannot be inserted: "approver_role" cannot be NULL.'; 
  END IF;
  IF NEW.approval_status IS NULL THEN
    RAISE EXCEPTION 'Booking Approval cannot be inserted: "approval_status" cannot be NULL.'; 
  END IF;
  IF NEW.approval_time IS NULL THEN
    RAISE EXCEPTION 'Booking Approval cannot be inserted: "approval_time" cannot be NULL.'; 
  END IF;

  -- Comments are optional, so not checked here.
  -- ID is auto-generated, not checked in BEFORE INSERT.
  -- If all checks pass, allow the insertion
  RETURN NEW;
END;
$$ LANGUAGE plpgsql
//