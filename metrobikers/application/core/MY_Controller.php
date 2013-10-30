<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class MY_Controller extends CI_Controller {

    public function __construct() {
        parent::__construct();
        if (!isset($_SESSION)) {
            session_start();
        }
       /* $lang = isset($_SESSION["CURRENT_LANGUAGE"]) ? $_SESSION["CURRENT_LANGUAGE"] : NULL;
        if ($lang == NULL)
            $lang = $this->config['language'];
        if (!$this->lang->load("messages", $lang))*/
            $this->lang->load("messages", "it-IT");
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
        $this->email->from(MAIL_USER, 'ECOmmuters');

        $this->email->to($to);

        $this->email->subject($subject);
        $this->email->message($message);

        $this->email->send();
    }
    protected function load_view($view, $data)
    {
        $data["view_content"] = $this->load->view($view, $data);
        $this->load->view("templates/template", $data);
    }
}
