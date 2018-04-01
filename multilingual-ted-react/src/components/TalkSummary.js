import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../common";
import {Col, message, Row} from 'antd';

class TalkSummary extends Component {
    constructor(props) {
        super(props);
        this.state = {
            talksNum: 0,
        }
    }
    
    componentWillMount() {
        axios.get(ajaxAddress + "/talksNum")
            .then(res => {
                let talksNum = res.data;
                this.setState({
                    talksNum
                })
            })
            .catch(e => {
                message.error('Fail to load talks num');
            });
    }
    
    render() {
        let {talksNum} = this.state;
        return (
            <div>
                <Row type="flex" justify="center">
                    <Col span={24}>
                        <div style={{fontSize: '50px', textAlign: 'center'}}>
                            <span><b>{talksNum}+ talks </b></span>
                            <span>to stir your curiosity</span>
                        </div>
                    </Col>
                </Row>
                <Row type="flex" justify="center">
                    <Col span={24}>
                        <div style={{fontSize: '24px', textAlign: 'center'}}>Find just the right one</div>
                    </Col>
                </Row>
            </div>
        )
    }
}

export default TalkSummary;