DROP TRIGGER IF EXISTS trg_check_booking_not_null ON booking; //
DROP TRIGGER IF EXISTS trg_check_approval_not_null ON booking_approval // 

CREATE TRIGGER trg_check_booking_not_null
BEFORE INSERT ON booking
FOR EACH ROW
EXECUTE FUNCTION check_booking_not_null_fields();
//

CREATE TRIGGER trg_check_approval_not_null
BEFORE INSERT ON booking_approval
FOR EACH ROW
EXECUTE FUNCTION check_approval_not_null_fields()
//