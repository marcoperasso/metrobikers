<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class MY_Controller extends CI_Controller {

    public function __construct() {
        parent::__construct();
    }

    function send_mail($to, $subject, $message) {
        $config = Array(
            'protocol' => 'smtp',
            'smtp_host' => 'ssl://smtp.gmail.com',
            'smtp_port' => 465,
            'smtp_user' => 'mtbgroupscout@gmail.com',
            'smtp_pass' => 'montoggio',
            'mailtype' => 'html',
            'charset' => 'iso-8859-1'
        );

        $this->load->library('email', $config);
        $this->email->set_newline("\r\n");
        $this->email->from('mtbgroupscout@gmail.com', 'Marco Perasso');

        $this->email->to($to);

        $this->email->subject($subject);
        $this->email->message($message);

        $this->email->send();
    }

}
