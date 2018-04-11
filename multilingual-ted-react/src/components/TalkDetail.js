import React, {Component} from 'react';
import {Col, Row} from 'antd';
import {ajaxAddress} from "../common";
import {message} from "antd/lib/index";
import axios from "axios/index";
import Main from './TalkDetailComponnets/Main';
import Right from './TalkDetailComponnets/Right';

class TalkDetail extends Component {
    constructor(props) {
        super(props);
        this.state = {
            talkId: this.props.params.talkId,
            languageCode: this.props.params.languageCode,
            talk: {},
        }
    }
    
    componentWillMount() {
        let {talkId, languageCode} = this.state;
        axios.get(`${ajaxAddress}/talk/${talkId}${languageCode == null ? `` : `?languageCode=${languageCode}`}`
        ).then(res => {
            let talk = res.data;
            this.setState({talk});
        }).catch(e => {
            message.error(`Fail to get talk ${talkId}`);
        });
    }
    
    render() {
        let {talk} = this.state;
        return (
            <div>
                <Row type="flex" justify="center">
                    <Col span={16}>
                        <Main talk={talk}/>
                    </Col>
                    <Col span={4}>
                        <Right talk={talk}/>
                    </Col>
                </Row>
            </div>
        )
    }
}

export default TalkDetail;