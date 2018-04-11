import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../common";
import {PubSub} from 'pubsub-js';
import {Col, message, Row} from 'antd';
import TopicFilter from "./FilterSubComponents/TopicFilter";
import LanguageFilter from "./FilterSubComponents/LanguageFilter";
import qs from 'qs';
import EventFilter from "./FilterSubComponents/EventFilter";
import SearchFilter from "./FilterSubComponents/SearchFilter";
import SortFilter from "./FilterSubComponents/SortFilter";

class Filter extends Component {
    postFilter = () => {
        let {searchFilter, topicFilter, languageFilter, eventFilter, sortFilter} = this.state;
        PubSub.publish('talksLoading', true);
        axios.post(`${ajaxAddress}/filterResults`, qs.stringify({
            filter: JSON.stringify({
                searchFilter: searchFilter,
                topicFilter: topicFilter,
                languageFilter: languageFilter,
                eventFilter: eventFilter,
                sortFilter: sortFilter,
            }),
        })).then(res => {
            PubSub.publish('talksLoading', false);
            PubSub.publish('filterTalkListResults', res.data);
        }).catch(e => {
            message.error('Fail to get talk list');
            PubSub.publish('talksLoading', false);
        });
    };
    
    constructor(props) {
        super(props);
        this.state = ({
            searchFilter: null,
            topicFilter: null,
            languageFilter: null,
            eventFilter: null,
            sortFilter: 1,
        })
    }
    
    componentWillMount() {
        this.postFilter();
        PubSub.subscribe('searchFilter', (msg, data) => {
            if ('' === data) {
                this.setState({searchFilter: null});
            } else {
                this.setState({searchFilter: data});
            }
            this.postFilter();
        });
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
        PubSub.subscribe('sortFilter', (msg, data) => {
            this.setState({sortFilter: data});
            this.postFilter();
        });
    }
    
    render() {
        let {sortFilter} = this.state;
        return (
            <div>
                <Row type="flex" justify="center" gutter={16}>
                    <Col span={3}><SearchFilter/></Col>
                    <Col span={6}><TopicFilter/></Col>
                    <Col span={3}><LanguageFilter/></Col>
                    <Col span={4}><EventFilter/></Col>
                    <Col span={2}><SortFilter defaultValue={sortFilter}/></Col>
                </Row>
            </div>
        )
    }
}

export default Filter;