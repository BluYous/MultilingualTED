import React, {Component} from 'react';
import {PubSub} from 'pubsub-js';
import {BackTop, Col, Divider, Pagination, Row, Spin} from 'antd';
import TalkFilterListPerPage from "./TalkFilterListPerPage";


class TalkFilterList extends Component {
    changePage = (pageNumber) => {
        let {talkList, pageSize} = this.state;
        let subTalkList = talkList.slice((pageNumber - 1) * pageSize, pageNumber * pageSize);
        this.setState({
            currentPage: pageNumber,
            subTalkList: subTalkList,
        });
        this.refs.backTop.scrollToTop();
    };
    
    constructor(props) {
        super(props);
        this.state = ({
            showMsg: true,
            msg: 'Loading...',
            talkList: [],
            subTalkList: [],
            currentPage: 1,
            pageSize: 12,
            languageFilter: null,
        })
    }
    
    componentWillMount() {
        PubSub.subscribe('talksLoading', (msg, data) => {
            if (data) {
                this.setState({
                    showMsg: data,
                    msg: 'Loading...'
                })
            }
        });
        PubSub.subscribe('languageFilter', (msg, data) => {
            this.setState({languageFilter: data});
        });
        let {currentPage, pageSize} = this.state;
        PubSub.subscribe('filterTalkListResults', (msg, data) => {
            if (data.length === 0) {
                this.setState({
                    showMsg: true,
                    msg: 'Sorry, no match found...',
                    talkList: data,
                });
            } else {
                let subTalkList = data.slice(0, currentPage * pageSize);
                this.setState({
                    showMsg: false,
                    talkList: data,
                    subTalkList: subTalkList,
                });
            }
        });
    }
    
    render() {
        let {talkList, subTalkList, msg, showMsg, currentPage, languageFilter, pageSize} = this.state;
        if (showMsg && msg === 'Loading...') {
            return (
                <div style={{textAlign: 'center'}}>
                    <Row type="flex" justify="center">
                        <Col span={18}>
                            <Spin size="large"/>
                        </Col>
                    </Row>
                </div>
            )
        } else if (showMsg) {
            return (
                <div style={{textAlign: 'center'}}>
                    <Row type="flex" justify="center">
                        <Col span={18}>
                            <h2>{msg}</h2>
                        </Col>
                    </Row>
                </div>
            )
        } else {
            return (
                <div>
                    <Row type="flex" justify="center">
                        <Col span={18}>
                            <div style={{display: 'none'}}><BackTop ref='backTop'/></div>
                            <TalkFilterListPerPage subTalkList={subTalkList}
                                                   languageFilter={languageFilter}/>
                            <Divider/>
                            <Pagination style={{textAlign: 'right'}} showQuickJumper total={talkList.length}
                                        defaultPageSize={pageSize}
                                        showTotal={total => `Total ${talkList.length} talk(s)`}
                                        current={currentPage}
                                        onChange={this.changePage}/>
                        </Col>
                    </Row>
                </div>
            
            )
        }
    }
}

export default TalkFilterList;