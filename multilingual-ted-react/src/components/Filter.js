import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../common";
import {PubSub} from 'pubsub-js';
import {Col, message, Row} from 'antd';
import TopicFilter from "./FilterSubComponents/TopicFilter";
import LanguageFilter from "./FilterSubComponents/LanguageFilter";
import qs from 'qs';
import EventFilter from "./FilterSubComponents/EventFilter";

class Filter extends Component {
    constructor(props) {
        super(props);
        this.state = ({
            topicFilter: null,
            languageFilter: null,
            eventFilter: null,
        })
    }
    
    componentWillMount() {
        PubSub.subscribe('topicsFilter', (msg, data) => {
            if (data.length !== 0) {
                this.setState({topicFilter: data});
            } else {
                this.setState({topicFilter: null});
            }
            this.postFilter();
        });
        PubSub.subscribe('languageFilter', (msg, data) => {
            this.setState({languageFilter: data});
            this.postFilter();
        });
        PubSub.subscribe('eventFilter', (msg, data) => {
            this.setState({eventFilter: data});
            this.postFilter();
        });
    }
    
    postFilter = () => {
        let {topicFilter, languageFilter, eventFilter} = this.state;
        axios.post(ajaxAddress + '/filterResults', qs.stringify({
            filter: JSON.stringify({
                topicFilter: topicFilter,
                languageFilter: languageFilter,
                eventFilter: eventFilter,
            }),
        }))
            .then(function (response) {
                message.info('TODO:Success post data');
            })
            .catch(function (error) {
                message.error('TODO:Fail to get data');
            });
    };
    
    render() {
        return (
            <div>
                <Row type="flex" justify="center">
                    <Col span={6}><TopicFilter/></Col>
                    <Col span={4}><LanguageFilter/></Col>
                    <Col span={4}><EventFilter/></Col>
                </Row>
            </div>
        )
    }
}

export default Filter;