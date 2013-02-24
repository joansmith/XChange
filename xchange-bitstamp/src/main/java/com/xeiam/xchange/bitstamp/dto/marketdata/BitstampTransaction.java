/**
 * Copyright (C) 2013 Matija Mazi
 * Copyright (C) 2013 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.xchange.bitstamp.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author Matija Mazi
 */
public final class BitstampTransaction {

  private final long date;
  private final int tid;
  private final BigDecimal price;
  private final BigDecimal amount;

  /**
   * Constructor
   *
   * @param date   Unix timestamp date and time
   * @param tid    Transaction id
   * @param price  BTC price
   * @param amount BTC amount
   */
  public BitstampTransaction(
    @JsonProperty("date") long date,
    @JsonProperty("tid") int tid,
    @JsonProperty("price") BigDecimal price,
    @JsonProperty("amount") BigDecimal amount) {

    this.date = date;
    this.tid = tid;
    this.price = price;
    this.amount = amount;
  }

  public int getTid() {

    return tid;
  }

  public BigDecimal getPrice() {

    return price;
  }

  public BigDecimal getAmount() {

    return amount;
  }

  public long getDate() {

    return date;
  }

  public BigDecimal calculateFeeBtc() {

    return roundUp(amount.multiply(new BigDecimal(.5))).divide(new BigDecimal(100.));
  }

  private BigDecimal roundUp(BigDecimal x) {

    long n = x.longValue();
    return new BigDecimal(x.equals(new BigDecimal(n)) ? n : n + 1);
  }

  public BigDecimal calculateFeeUsd() {

    return calculateFeeBtc().multiply(price);
  }

  @Override
  public String toString() {

    return "Transaction [date=" + date + ", tid=" + tid + ", price=" + price + ", amount=" + amount + "]";
  }

}
