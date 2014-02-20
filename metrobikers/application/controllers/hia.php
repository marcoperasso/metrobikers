<?php

if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}
        const SUCCESS = 0;
        const CUSTOM_ERROR = 1;
        const USER_NOT_LOGGED = 2;
        const NO_RECEIVER_IDS = 3;

        const MSG_REQUEST_CONTACT = 0;
        const MSG_ACCEPT_CONTACT = 1;
        const MSG_REJECT_CONTACT = 2;
        const MSG_REMOVE_CONTACT = 3;
        const MSG_WRONG_PASSWORD = 4;
        const MSG_MESSAGE = 5;
        const MSG_POSITION = 6;

class Hia extends CI_Controller {

    var $user;

    public function __construct() {
        parent::__construct();
        if (!isset($_SESSION)) {
            session_start();
        }
        $this->load->model('HIA_User_model');

        $this->user = (isset($_SESSION) && isset($_SESSION['user'])) ? unserialize($_SESSION["user"]) : NULL;
    }

    public function test($phone, $message) {
        if ($this->HIA_User_model->get_user('+393383681001')) {
            $old = $this->user;
            $this->user = clone ($this->HIA_User_model);
            if ($this->HIA_User_model->get_user($phone)) {
                $this->internal_message_to_user($this->HIA_User_model->phone, MSG_MESSAGE, TRUE, array("message" => $message, "time" => time()));
            } else {
                echo "User not found!";
            }
            $this->user = $old;
        } else {
            echo "User not found!";
        }
    }

    protected function send_mail($to, $subject, $message) {
        $config = Array(
            'protocol' => 'smtp',
            'smtp_host' => MAIL_HOST,
            'smtp_port' => MAIL_PORT,
            'smtp_user' => MAIL_USER,
            'smtp_pass' => MAIL_PASSWORD,
            'mailtype' => 'html',
            'charset' => 'utf-8',
            'mailtype' => 'html'
        );

        $this->load->library('email', $config);
        $this->email->set_newline("\r\n");
        $this->email->set_crlf("\r\n");
        $this->email->from(MAIL_USER, 'Here I Am');

        $this->email->to($to);

        $this->email->subject($subject);
        $this->email->message($message);

        $this->email->send();
    }

    private function send_message($registrationIDs, $data, $collapse_key = NULL) {
        $apiKey = "AIzaSyC2SzSst-NVCnnUKlGegbarNe6SapTgDnk";

        // Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';

        $fields = array(
            'registration_ids' => $registrationIDs,
            'data' => $data
        );
        if ($collapse_key != NULL) {
            $fields['collapse_key'] = $collapse_key;
        }
        $headers = array(
            'Authorization: key=' . $apiKey,
            'Content-Type: application/json'
        );

        // Open connection
        $ch = curl_init();

        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);

        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

        // Execute post
        $result = curl_exec($ch);

        // Close connection
        curl_close($ch);

        return $result;
    }

    public function save_user() {
        $phone = $this->input->post('phone');
        $newuser = $this->input->post('newuser');
        $existing = $this->HIA_User_model->get_user($phone);
        $response = array('success' => TRUE);
        if ($existing) {
            if ($newuser) {
                $response["message"] = "User already existing";
                $response["success"] = FALSE;
                $this->output->set_content_type('application/json')->set_output(json_encode($response));
                return;
            }
        }
        $this->HIA_User_model->mail = $this->input->post('email');
        $this->HIA_User_model->phone = $phone;

        $this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);

        $this->HIA_User_model->password = $bcrypt->hash($this->input->post('pwd'));
        $this->HIA_User_model->regid = $this->input->post('regid');

        if ($existing) {
            $this->HIA_User_model->update_user();
        } else {
            $this->HIA_User_model->create_user();
            $this->send_mail(
                    "marco.perasso@gmail.com", "Nuovo utente", $this->HIA_User_model->phone);
            set_user($this->HIA_User_model);
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($response));
    }

    public function login() {
        $pwd = $this->input->post('pwd');
        $phone = $this->input->post('phone');

        $this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);

        $success = $this->HIA_User_model->get_user($phone) && $bcrypt->verify($pwd, $this->HIA_User_model->password);
        $response = array('success' => $success, 'version' => 1);
        if ($success) {
            set_user($this->HIA_User_model);
            $response["mail"] = $this->HIA_User_model->mail;
        } else {
            $response["message"] = "Login failed. Invalid user or password";
        }

        $this->output->set_content_type('application/json')->set_output(json_encode($response));
    }

    public function user_logged() {
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode(array('logged' => $this->user !== NULL)));
    }

    public function update_position() {
        $json = $this->input->post("data");
        $point = json_decode($json);
        if ($point) {
            $this->load->model("HIA_User_position_model");
            $this->load->model("HIA_Connections_model");

            $this->db->trans_begin();

            $this->HIA_User_position_model->phone = $point->phone;
            $this->HIA_User_position_model->lat = $point->lat;
            $this->HIA_User_position_model->lon = $point->lon;
            $this->HIA_User_position_model->gps = $point->gps ? 1 : 0;
            $this->HIA_User_position_model->time = date('Y-m-d H:i:s', $point->time);
            $this->HIA_User_position_model->save_position();

            $this->HIA_Connections_model->purge($point->phone); 
            foreach ($point->users as $phone) {
                $this->HIA_Connections_model->phonefrom = $point->phone;
                $this->HIA_Connections_model->phoneto = $phone;
                $this->HIA_Connections_model->create_connection();

                $this->internal_message_to_user($phone, MSG_POSITION, FALSE, array("position" => $point));
            }
            $this->db->trans_commit();
            $response = array('saved' => TRUE);
        } else {
            $response = array('saved' => FALSE);
        }


        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function get_positions() {
        $phone = $this->user == NULL ? "" : $this->user->phone;
        $this->get_positions_by_userid($phone);
    }

    public function get_positions_by_userid($phone) {
        $this->load->model("HIA_User_position_model");
        $this->HIA_User_position_model->purge_positions();
        $response = $this->HIA_User_position_model->get_positions($phone);

        if ($response) {
            foreach ($response as &$point) {
                $point["time"] = strtotime($point["time"]);
                $point["gps"] = $point["gps"] == 1 ? TRUE : FALSE;
            }
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function respond_to_user() {

        $this->internal_message_to_user($this->input->post("userphone"), $this->input->post("responsecode"));
    }

    public function contact_user() {
        $this->internal_message_to_user($this->input->post("userphone"), MSG_REQUEST_CONTACT, TRUE, array("securetoken" => $this->input->post("securetoken")));
    }

    public function disconnect_user() {
        $this->internal_message_to_user($this->input->post("userphone"), MSG_REMOVE_CONTACT);
    }

    public function message_to_user() {
        $message = $this->input->post('message');
        $phone = $this->input->post('userphone');
        $time = $this->input->post('time');

        $this->internal_message_to_user($phone, MSG_MESSAGE, TRUE, array("message" => $message, "time" => $time));
    }

    public function verify_registration() {
        $json = $this->input->post("data");
        $phones = json_decode($json);
        foreach ($phones->users as &$phone) {
            $phone = $this->HIA_User_model->get_user($phone);
        }
        $response = array('result' => SUCCESS, 'users' => $phones->users);
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    private function get_collapse_key($response_code) {
        switch ($response_code) {
            case MSG_REQUEST_CONTACT: return "MSG_REQUEST_CONTACT";
            case MSG_POSITION: return "MSG_POSITION";
            default : return NULL;
        }
    }

    private function internal_message_to_user($phone, $response_code, $do_output = TRUE, $args = array()) {
        if ($this->user !== NULL) {
            $ids = "";
            if ($this->HIA_User_model->get_user($phone))
                $ids = $this->HIA_User_model->regid;
            if ($ids == "") {
                $response->result = NO_RECEIVER_IDS;
            } else {
                $regids = array();
                array_push($regids, $ids);
                $ar = array(
                    'msgtype' => $response_code,
                    'touserphone' => $phone,
                    'fromuserphone' => $this->user->phone
                );
                $ar = array_merge($ar, $args);
                $result = $this->send_message($regids, $ar, $this->get_collapse_key($response_code));
                $response = array('result' => SUCCESS, "gcmresponse" => json_decode($result));
            }
        } else {
            $response = array('result' => USER_NOT_LOGGED);
        }
        if ($do_output) {
            $this->output
                    ->set_content_type('application/json')
                    ->set_output(json_encode($response));
        }
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
